package com.superappzw.services

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.superappzw.model.StoreRatingAggregate
import com.superappzw.model.StoreReviewModel
import kotlinx.coroutines.tasks.await

class ReviewService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ── Fetch reviews ─────────────────────────────────────────────────────────

    suspend fun fetchReviews(storeID: String): List<StoreReviewModel> {
        val snapshot = db
            .collection("user-reviews")
            .document(storeID)
            .collection("reviews")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toStoreReviewModel()?.copy(id = doc.id)
        }
    }

    // ── Fetch aggregate rating ────────────────────────────────────────────────

    suspend fun fetchRatingAggregate(storeID: String): StoreRatingAggregate? {
        val doc = db
            .collection("user-reviews")
            .document(storeID)
            .collection("aggregates")
            .document("rating")
            .get()
            .await()

        return doc.toStoreRatingAggregate()
    }

    // ── Submit review ─────────────────────────────────────────────────────────

    suspend fun submitReview(
        storeID: String,
        storeOwnerUID: String,
        comment: String,
        rating: Int,
    ) {
        val currentUser = auth.currentUser
            ?: throw ReviewError.NotAuthenticated

        if (currentUser.uid == storeOwnerUID) throw ReviewError.SelfReview

        val reviewerUID = currentUser.uid
        val userService = UserService.getInstance()
        val profile = userService.fetchProfile(reviewerUID)

        val reviewerName = profile.fullName
            .takeIf { it.isNotBlank() }
            ?: currentUser.displayName
            ?: "Anonymous"

        val reviewerImageURL = profile.profileImageURL ?: ""

        val storeRef = db.collection("user-reviews").document(storeID)
        val reviewRef = storeRef.collection("reviews").document(reviewerUID)
        val aggregateRef = storeRef.collection("aggregates").document("rating")

        val reviewData = mapOf(
            "reviewerUID" to reviewerUID,
            "reviewerName" to reviewerName,
            "reviewerImageURL" to reviewerImageURL,
            "comment" to comment,
            "rating" to rating,
            "createdAt" to FieldValue.serverTimestamp(),
        )

        // ── Firestore transaction — mirrors Swift's runTransaction ────────────
        db.runTransaction { transaction ->

            // Read existing review (detect edit vs new)
            val existingReviewSnap = transaction.get(reviewRef)

            // Read existing aggregate (may not exist yet)
            val aggregateSnap = transaction.get(aggregateRef)

            var currentAverage = 0.0
            var currentTotal = 0
            var oldRating = 0
            val isEdit = existingReviewSnap.exists()

            // Parse existing aggregate
            aggregateSnap.toStoreRatingAggregate()?.let {
                currentAverage = it.averageRating
                currentTotal = it.totalReviews
            }

            // Parse old rating if editing
            if (isEdit) {
                oldRating = (existingReviewSnap.getLong("rating") ?: 0L).toInt()
            }

            // Recalculate average
            val newTotal: Int
            val newAverage: Double

            if (isEdit) {
                val totalRatingPoints = currentAverage * currentTotal
                val updated = totalRatingPoints - oldRating + rating
                newTotal = currentTotal
                newAverage = if (currentTotal > 0) updated / currentTotal else rating.toDouble()
            } else {
                val totalRatingPoints = currentAverage * currentTotal + rating
                newTotal = currentTotal + 1
                newAverage = totalRatingPoints / newTotal
            }

            // Write review (creates or overwrites)
            transaction.set(reviewRef, reviewData)

            // Write aggregate (merge: true — creates if not exists)
            transaction.set(
                aggregateRef,
                mapOf(
                    "averageRating" to newAverage,
                    "totalReviews" to newTotal,
                ),
                com.google.firebase.firestore.SetOptions.merge(),
            )

        }.await()
    }

    // ── Has current user already reviewed ─────────────────────────────────────

    suspend fun hasCurrentUserReviewed(storeID: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            val doc = db
                .collection("user-reviews")
                .document(storeID)
                .collection("reviews")
                .document(uid)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    // ── Document mapping helpers ───────────────────────────────────────────────

    private fun com.google.firebase.firestore.DocumentSnapshot.toStoreReviewModel(): StoreReviewModel? {
        val data = data ?: return null
        return StoreReviewModel(
            id = id,
            reviewerUID = data["reviewerUID"] as? String ?: return null,
            reviewerName = data["reviewerName"] as? String ?: return null,
            reviewerImageURL = data["reviewerImageURL"] as? String,
            comment = data["comment"] as? String ?: return null,
            rating = (data["rating"] as? Long)?.toInt() ?: return null,
            createdAt = data["createdAt"] as? Timestamp,
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toStoreRatingAggregate(): StoreRatingAggregate? {
        val data = data ?: return null
        return StoreRatingAggregate(
            averageRating = (data["averageRating"] as? Double) ?: return null,
            totalReviews = (data["totalReviews"] as? Long)?.toInt() ?: return null,
        )
    }
}

// ── Review errors ─────────────────────────────────────────────────────────────

sealed class ReviewError(message: String) : Exception(message) {
    object SelfReview : ReviewError("You cannot review your own store.")
    object NotAuthenticated : ReviewError("You must be signed in to leave a review.")
}