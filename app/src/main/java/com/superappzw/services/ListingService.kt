package com.superappzw.services

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.superappzw.ui.store.StoreListing
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ListingService {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ── Publish listing ───────────────────────────────────────────────────────

    suspend fun publishListing(
        type: ListingType,
        category: String,
        currency: String,
        title: String,
        priceText: String?,
        description: String,
        imageData: ByteArray?,
        userId: String,
        location: String = "",          // ← added
    ) {
        ensureListingsDocumentExists(userId)
        publishListingData(
            type = type,
            category = category,
            currency = currency,
            title = title,
            priceText = priceText,
            description = description,
            imageData = imageData,
            userId = userId,
            location = location,
        )
    }

    private suspend fun publishListingData(
        type: ListingType,
        category: String,
        currency: String,
        title: String,
        priceText: String?,
        description: String,
        imageData: ByteArray?,
        userId: String,
        location: String = "",          // ← added
    ) {
        val userDocRef = db.collection("listings").document(userId)
        val itemCode = generateUniqueItemCode()
        val clientTimestamp = Timestamp.now()

        if (type == ListingType.PRODUCT) {
            val imageURL = if (imageData != null) uploadImage(imageData, userId) else ""
            val normalizedPrice = priceText?.replace(",", ".") ?: ""

            val listing = mapOf(
                "title"       to title,
                "category"    to category,
                "currency"    to currency,
                "price"       to normalizedPrice,
                "description" to description,
                "itemCode"    to itemCode,
                "imageURL"    to imageURL,
                "viewCount"   to 0,
                "createdAt"   to clientTimestamp,
                "type"        to type.value,
                "location"    to location,      // ← added
            )

            userDocRef.update("myListings", FieldValue.arrayUnion(listing)).await()

        } else {
            userDocRef.update("myServices", FieldValue.arrayUnion(title)).await()
        }
    }

    // ── Generate item code ────────────────────────────────────────────────────

    private fun generateUniqueItemCode(): String {
        val timestamp = System.currentTimeMillis() / 1000
        val random = (1000..9999).random()
        return "ISA-ZW-$timestamp-$random"
    }

    // ── Upload image ──────────────────────────────────────────────────────────

    private suspend fun uploadImage(imageData: ByteArray, userId: String): String {
        val imageRef = storage.reference
            .child("listings/$userId/${UUID.randomUUID()}.jpg")

        val metadata = com.google.firebase.storage.StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        imageRef.putBytes(imageData, metadata).await()
        return imageRef.downloadUrl.await().toString()
    }

    // ── Record view ───────────────────────────────────────────────────────────

    suspend fun recordView(itemCode: String, ownerUserID: String) {
        val currentUserID = auth.currentUser?.uid ?: return
        if (currentUserID == ownerUserID) return

        val ownerDocument = db.collection("listings").document(ownerUserID)
        val viewDocumentID = "${currentUserID}_${itemCode}"
        val viewDocument = ownerDocument.collection("views").document(viewDocumentID)

        val viewSnapshot = viewDocument.get().await()
        if (viewSnapshot.exists()) return

        val ownerSnapshot = ownerDocument.get().await()
        val data = ownerSnapshot.data ?: return

        @Suppress("UNCHECKED_CAST")
        val myListings = (data["myListings"] as? List<Map<String, Any>>)
            ?.map { it.toMutableMap() }
            ?.toMutableList() ?: return

        val index = myListings.indexOfFirst { it["itemCode"] as? String == itemCode }
        if (index == -1) return

        val currentCount = (myListings[index]["viewCount"] as? Long)?.toInt() ?: 0
        myListings[index]["viewCount"] = currentCount + 1

        val batch = db.batch()
        batch.set(
            viewDocument,
            mapOf(
                "itemCode"     to itemCode,
                "ownerUserID"  to ownerUserID,
                "viewerUserID" to currentUserID,
                "viewedAt"     to FieldValue.serverTimestamp(),
            )
        )
        batch.set(
            ownerDocument,
            mapOf("myListings" to myListings),
            com.google.firebase.firestore.SetOptions.merge(),
        )
        batch.commit().await()
    }

    // ── Fetch user listings ───────────────────────────────────────────────────

    suspend fun fetchUserListings(userId: String): List<StoreListing> {
        val snapshot = db.collection("listings").document(userId).get().await()
        val data = snapshot.data ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        val myListings = data["myListings"] as? List<Map<String, Any>> ?: return emptyList()

        return myListings.mapNotNull { it.toStoreListing(ownerUserID = userId) }
    }

    // ── Fetch all listings ────────────────────────────────────────────────────

    suspend fun fetchAllListings(userId: String): List<StoreListing> {
        return fetchUserListings(userId)
    }

    // ── Fetch user services ───────────────────────────────────────────────────

    suspend fun fetchUserServices(userId: String): List<String> {
        val snapshot = db.collection("listings").document(userId).get().await()
        val data = snapshot.data ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        return data["myServices"] as? List<String> ?: emptyList()
    }

    // ── Add service listing ───────────────────────────────────────────────────

    suspend fun addServiceListing(service: String) {
        val userID = auth.currentUser?.uid
            ?: throw ListingError.Unauthenticated

        ensureListingsDocumentExists(userID)

        db.collection("listings").document(userID)
            .update("myServices", FieldValue.arrayUnion(service))
            .await()
    }

    // ── Fetch listings by category ────────────────────────────────────────────

    suspend fun fetchListingsByCategory(categoryName: String): List<StoreListing> {
        val snapshot = db.collection("listings").get().await()

        return snapshot.documents.flatMap { document ->
            val data = document.data ?: return@flatMap emptyList()
            val userId = document.id

            @Suppress("UNCHECKED_CAST")
            val myListings = data["myListings"] as? List<Map<String, Any>>
                ?: return@flatMap emptyList()

            myListings
                .filter { it["category"] as? String == categoryName }
                .mapNotNull { it.toStoreListing(ownerUserID = userId) }
        }
    }

    // ── Delete listing ────────────────────────────────────────────────────────

    suspend fun deleteListing(itemCode: String) {
        val currentUser = auth.currentUser
            ?: throw ListingError.Unauthenticated

        val userID = currentUser.uid
        val userDocument = db.collection("listings").document(userID)

        val snapshot = userDocument.get().await()
        val data = snapshot.data
            ?: throw ListingError.NotFound("No listings document found for the current user.")

        @Suppress("UNCHECKED_CAST")
        val myListings = data["myListings"] as? List<Map<String, Any>>
            ?: throw ListingError.NotFound("myListings field is missing or has an unexpected format.")

        val targetListing = myListings.firstOrNull { it["itemCode"] as? String == itemCode }
            ?: throw ListingError.NotFound("No listing found with itemCode '$itemCode'.")

        val imageURL = targetListing["imageURL"] as? String
            ?: throw ListingError.InvalidData("The listing does not have a valid imageURL.")

        val updatedListings = myListings.filter { it["itemCode"] as? String != itemCode }
        userDocument.set(
            mapOf("myListings" to updatedListings),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()

        if (imageURL.isNotBlank()) {
            val imageStorageRef = storage.getReferenceFromUrl(imageURL)
            if (!imageStorageRef.path.contains(userID)) {
                throw ListingError.PermissionDenied(
                    "The image path does not belong to the current user."
                )
            }
            imageStorageRef.delete().await()
        }
    }

    // ── Delete service listing ────────────────────────────────────────────────

    suspend fun deleteServiceListing(service: String) {
        val userID = auth.currentUser?.uid
            ?: throw ListingError.Unauthenticated

        db.collection("listings").document(userID)
            .update("myServices", FieldValue.arrayRemove(service))
            .await()
    }

    // ── Ensure listings document exists ───────────────────────────────────────

    private suspend fun ensureListingsDocumentExists(userID: String) {
        val userDocRef = db.collection("listings").document(userID)
        val snapshot = userDocRef.get().await()
        if (snapshot.exists()) return

        userDocRef.set(
            mapOf(
                "myListings" to emptyList<Any>(),
                "myServices" to emptyList<Any>(),
                "userId"     to userID,
                "createdAt"  to Timestamp.now(),
            ),
            com.google.firebase.firestore.SetOptions.merge(),
        ).await()
    }

    // ── Mapping helper ────────────────────────────────────────────────────────

    private fun Map<String, Any>.toStoreListing(ownerUserID: String): StoreListing? {
        val title          = this["title"]       as? String ?: return null
        val itemCode       = this["itemCode"]     as? String ?: return null
        val imageURLString = this["imageURL"]     as? String ?: return null
        val priceString    = this["price"]        as? String ?: return null
        val currency       = this["currency"]     as? String ?: return null
        val viewCount      = (this["viewCount"]   as? Long)?.toInt() ?: 0
        val description    = this["description"]  as? String ?: ""
        val location       = this["location"]     as? String ?: ""      // ← added
        val isNegotiable   = priceString.trim() == "Negotiable"         // ← added
        val price          = if (isNegotiable) 0.0 else priceString.toDoubleOrNull() ?: 0.0

        return StoreListing(
            title        = title,
            description  = description,
            price        = price,
            currency     = currency,
            itemCode     = itemCode,
            imageURL     = imageURLString.takeIf { it.isNotBlank() },
            viewCount    = viewCount,
            ownerUserID  = ownerUserID,
            location     = location,        // ← added
            isNegotiable = isNegotiable,    // ← added
        )
    }
}

// ── ListingType ───────────────────────────────────────────────────────────────

enum class ListingType(val value: String) {
    PRODUCT("product"),
    SERVICE("service"),
}

// ── ListingError ──────────────────────────────────────────────────────────────

sealed class ListingError(message: String) : Exception(message) {
    object Unauthenticated : ListingError("No authenticated user found.")
    class NotFound(message: String) : ListingError(message)
    class InvalidData(message: String) : ListingError(message)
    class PermissionDenied(message: String) : ListingError(message)
}