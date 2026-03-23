package com.superappzw.services

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.superappzw.model.MyReviewModel
import com.superappzw.model.StoreRatingAggregate
import com.superappzw.model.StoreReviewModel
import kotlinx.coroutines.tasks.await

class ReviewService {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ── Fetch reviews for a store ─────────────────────────────────────────────

    suspend fun fetchReviews(storeID: String): List<StoreReviewModel> {
        // user-reviews requires auth — guests get an empty list, matching iOS behaviour
        if (auth.currentUser == null) return emptyList()
        return try {
            val snapshot = db
                .collection("user-reviews")
                .document(storeID)
                .collection("reviews")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toStoreReviewModel()?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Fetch aggregate rating ────────────────────────────────────────────────

    suspend fun fetchRatingAggregate(storeID: String): StoreRatingAggregate? {
        // user-reviews requires auth — guests get null, matching iOS behaviour
        if (auth.currentUser == null) return null
        return try {
            val doc = db
                .collection("user-reviews")
                .document(storeID)
                .collection("aggregates")
                .document("rating")
                .get()
                .await()
            doc.toStoreRatingAggregate()
        } catch (e: Exception) {
            null
        }
    }

    // ── Has current user already reviewed ─────────────────────────────────────

    suspend fun hasCurrentUserReviewed(storeID: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            db.collection("user-reviews")
                .document(storeID)
                .collection("reviews")
                .document(uid)
                .get()
                .await()
                .exists()
        } catch (e: Exception) {
            false
        }
    }

    // ── Submit review (upsert — reviewerUID is the document ID) ──────────────

    suspend fun submitReview(
        storeID: String,
        storeOwnerUID: String,
        comment: String,
        rating: Int,
    ) {
        val currentUser = auth.currentUser ?: throw ReviewError.NotAuthenticated
        if (currentUser.uid == storeOwnerUID) throw ReviewError.SelfReview

        val reviewerUID      = currentUser.uid
        val profile          = UserService.getInstance().fetchProfile(reviewerUID)
        val reviewerName     = profile.fullName.takeIf { it.isNotBlank() }
            ?: currentUser.displayName
            ?: "Anonymous"
        val reviewerImageURL = profile.profileImageURL ?: ""

        val storeRef     = db.collection("user-reviews").document(storeID)
        val reviewRef    = storeRef.collection("reviews").document(reviewerUID)
        val aggregateRef = storeRef.collection("aggregates").document("rating")

        val reviewData = mapOf(
            "reviewerUID"      to reviewerUID,
            "reviewerName"     to reviewerName,
            "reviewerImageURL" to reviewerImageURL,
            "comment"          to comment,
            "rating"           to rating,
            "createdAt"        to FieldValue.serverTimestamp(),
        )

        db.runTransaction { transaction ->
            val existingSnap  = transaction.get(reviewRef)
            val aggregateSnap = transaction.get(aggregateRef)

            val isEdit       = existingSnap.exists()
            val oldRating    = if (isEdit) (existingSnap.getLong("rating")?.toInt() ?: 0) else 0
            val currentAvg   = aggregateSnap.getDouble("averageRating") ?: 0.0
            val currentTotal = aggregateSnap.getLong("totalReviews")?.toInt() ?: 0

            val newTotal: Int
            val newAverage: Double

            if (isEdit) {
                val updated = currentAvg * currentTotal - oldRating + rating
                newTotal   = currentTotal
                newAverage = if (currentTotal > 0) updated / currentTotal else rating.toDouble()
            } else {
                val totalPoints = currentAvg * currentTotal + rating
                newTotal   = currentTotal + 1
                newAverage = totalPoints / newTotal
            }

            transaction.set(reviewRef, reviewData)
            transaction.set(
                aggregateRef,
                mapOf("averageRating" to newAverage, "totalReviews" to newTotal),
                com.google.firebase.firestore.SetOptions.merge(),
            )
        }.await()
    }

    // ── Delete review from store profile ──────────────────────────────────────

    suspend fun deleteReview(storeID: String, reviewID: String) {
        val uid = auth.currentUser?.uid ?: throw ReviewError.NotAuthenticated
        if (reviewID != uid) throw ReviewError.PermissionDenied

        val storeRef     = db.collection("user-reviews").document(storeID)
        val reviewRef    = storeRef.collection("reviews").document(reviewID)
        val aggregateRef = storeRef.collection("aggregates").document("rating")

        reviewRef.delete().await()
        recalculateAggregate(storeRef, aggregateRef)
    }

    // ── Fetch current user's reviews across all stores (collection group) ─────

    suspend fun fetchMyReviews(): List<MyReviewModel> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snapshot = db.collectionGroup("reviews")
            .whereEqualTo("reviewerUID", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        // Deduplicate — keep only the most recent review per store.
        // Guards against stale docs written before reviewerUID-as-docID convention.
        val seenStoreIDs = mutableSetOf<String>()

        return snapshot.documents.mapNotNull { doc ->
            val storeID = doc.reference.parent.parent?.id ?: return@mapNotNull null
            if (storeID.isBlank()) return@mapNotNull null
            if (!seenStoreIDs.add(storeID)) return@mapNotNull null

            val review = doc.toStoreReviewModel()?.copy(id = doc.id) ?: return@mapNotNull null
            MyReviewModel(storeID = storeID, review = review)
        }
    }

    // ── Delete own review (called from MyReviewsView) ─────────────────────────

    suspend fun deleteMyReview(storeID: String, reviewID: String) {
        val uid = auth.currentUser?.uid ?: throw ReviewError.NotAuthenticated
        if (reviewID != uid) throw ReviewError.PermissionDenied

        val storeRef     = db.collection("user-reviews").document(storeID)
        val reviewRef    = storeRef.collection("reviews").document(reviewID)
        val aggregateRef = storeRef.collection("aggregates").document("rating")

        reviewRef.delete().await()
        recalculateAggregate(storeRef, aggregateRef)
    }

    // ── Shared aggregate recalculation ────────────────────────────────────────

    private suspend fun recalculateAggregate(
        storeRef: com.google.firebase.firestore.DocumentReference,
        aggregateRef: com.google.firebase.firestore.DocumentReference,
    ) {
        val remaining = storeRef.collection("reviews").get().await()
        val ratings   = remaining.documents.mapNotNull { it.getLong("rating")?.toInt() }

        if (ratings.isEmpty()) {
            aggregateRef.set(mapOf("averageRating" to 0.0, "totalReviews" to 0)).await()
        } else {
            aggregateRef.set(
                mapOf(
                    "averageRating" to ratings.average(),
                    "totalReviews"  to ratings.size,
                )
            ).await()
        }
    }

    // ── Document mapping helpers ───────────────────────────────────────────────

    private fun com.google.firebase.firestore.DocumentSnapshot.toStoreReviewModel(): StoreReviewModel? {
        val d = data ?: return null
        return StoreReviewModel(
            id               = id,
            reviewerUID      = d["reviewerUID"]      as? String ?: return null,
            reviewerName     = d["reviewerName"]      as? String ?: return null,
            reviewerImageURL = d["reviewerImageURL"]  as? String,
            comment          = d["comment"]           as? String ?: return null,
            rating           = (d["rating"]           as? Long)?.toInt() ?: return null,
            createdAt        = d["createdAt"]         as? Timestamp,
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toStoreRatingAggregate(): StoreRatingAggregate? {
        val d = data ?: return null
        return StoreRatingAggregate(
            averageRating = d["averageRating"] as? Double ?: return null,
            totalReviews  = (d["totalReviews"] as? Long)?.toInt() ?: return null,
        )
    }
}

// ── Errors ────────────────────────────────────────────────────────────────────

sealed class ReviewError(message: String) : Exception(message) {
    object SelfReview       : ReviewError("You cannot review your own store.")
    object NotAuthenticated : ReviewError("You must be signed in to leave a review.")
    object PermissionDenied : ReviewError("You do not have permission to perform this action.")
}
