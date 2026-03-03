package com.superappzw.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import android.graphics.Bitmap
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

data class UserProfile(
    val fullName: String = "",
    val profileImageURL: String? = null,
)

data class PackageLimitModel(
    val id: String,
    val productLimit: Int,
    val serviceLimit: Int,
) {
    companion object {
        fun fromData(id: String, data: Map<String, Any>): PackageLimitModel? {
            val productLimit = (data["product_limit"] as? Long)?.toInt() ?: return null
            val serviceLimit = (data["service_limit"] as? Long)?.toInt() ?: return null
            return PackageLimitModel(
                id = id,
                productLimit = productLimit,
                serviceLimit = serviceLimit,
            )
        }
    }
}

class UserService private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: UserService? = null

        fun getInstance(): UserService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserService().also { INSTANCE = it }
            }
        }
    }

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // ── Create user document ──────────────────────────────────────────────────

    suspend fun createUserDocument(
        uid: String,
        firstName: String,
        lastName: String,
        email: String,
    ) {
        val userData = mapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "email" to email,
            "phone_number" to null,
            "did_accept_terms" to true,
            "subscription_is_active" to false,
            "suburb" to null,
            "location" to null,
            "device_type" to "Android",
            "virtual_shop_name" to null,
            "profile_image_url" to null,
            "is_profile_complete" to false,
            "package_id" to "standard",
            "product_post_count" to 0,
            "service_post_count" to 0,
            "created_at" to FieldValue.serverTimestamp(),
            "updated_at" to FieldValue.serverTimestamp(),
        )

        db.collection("users").document(uid).set(userData).await()
    }

    // ── Fetch profile ─────────────────────────────────────────────────────────

    suspend fun fetchProfile(uid: String): UserProfile {
        val doc = db.collection("users").document(uid).get().await()
        val data = doc.data ?: throw Exception("User document not found")

        val firstName = data["first_name"] as? String ?: ""
        val lastName = data["last_name"] as? String ?: ""

        return UserProfile(
            fullName = "$firstName $lastName".trim(),
            profileImageURL = data["profile_image_url"] as? String,
        )
    }

    // ── Update profile ────────────────────────────────────────────────────────

    suspend fun updateProfile(
        uid: String,
        firstName: String,
        lastName: String,
        suburb: String,
        location: String,
        phoneNumber: String,
        virtualShopName: String,
    ) {
        val isComplete = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                location.isNotBlank() &&
                suburb.isNotBlank() &&
                virtualShopName.isNotBlank()

        val data = mapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "suburb" to suburb,
            "location" to location,
            "phone_number" to phoneNumber,
            "virtual_shop_name" to virtualShopName,
            "is_profile_complete" to isComplete,
            "updated_at" to FieldValue.serverTimestamp(),
        )

        db.collection("users").document(uid).update(data).await()
    }

    // ── Fetch package limit ───────────────────────────────────────────────────

    suspend fun fetchPackageLimit(packageID: String): PackageLimitModel {
        val normalizedID = packageID.lowercase()
        val doc = db.collection("packages").document(normalizedID).get().await()
        val data = doc.data ?: throw Exception("Invalid package data")

        return PackageLimitModel.fromData(doc.id, data)
            ?: throw Exception("Invalid package data")
    }

    // ── Upload profile image ──────────────────────────────────────────────────
    // Mirrors Swift: crops to square, resizes to 100x100, uploads as JPEG,
    // then writes the download URL back to Firestore.

    suspend fun uploadProfileImage(uid: String, bitmap: Bitmap): String {
        // Step 1: Crop to square and resize to 100x100
        val resized = cropAndResize(bitmap, 100, 100)

        // Step 2: Compress to JPEG bytes
        val outputStream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val imageData = outputStream.toByteArray()

        // Step 3: Build Storage reference — overwriting always replaces previous
        val storageRef = storage.reference
            .child("profile-images/$uid/$uid.jpg")

        val metadata = com.google.firebase.storage.StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        // Step 4: Upload
        storageRef.putBytes(imageData, metadata).await()

        // Step 5: Fetch download URL
        val downloadURL = storageRef.downloadUrl.await().toString()

        // Step 6: Write URL back to Firestore
        db.collection("users").document(uid).update(
            "profile_image_url", downloadURL
        ).await()

        return downloadURL
    }

    // ── Fetch post counts ─────────────────────────────────────────────────────

    suspend fun fetchPostCounts(uid: String): Triple<Int, Int, String> {
        val doc = db.collection("users").document(uid).get().await()
        val data = doc.data ?: throw Exception("User not found")

        val products = (data["product_post_count"] as? Long)?.toInt() ?: 0
        val services = (data["service_post_count"] as? Long)?.toInt() ?: 0
        val packageID = (data["package_id"] as? String ?: "standard").lowercase()

        return Triple(products, services, packageID)
    }

    // ── Check profile complete ────────────────────────────────────────────────

    suspend fun isProfileComplete(uid: String): Boolean {
        val doc = db.collection("users").document(uid).get().await()
        return doc.data?.get("is_profile_complete") as? Boolean ?: false
    }

    // ── Increment post count ──────────────────────────────────────────────────

    suspend fun incrementPostCount(uid: String, isService: Boolean) {
        val field = if (isService) "service_post_count" else "product_post_count"
        db.collection("users").document(uid).update(
            field, FieldValue.increment(1L)
        ).await()
    }

    // ── Image crop/resize helper ──────────────────────────────────────────────

    private fun cropAndResize(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val xOffset = (bitmap.width - size) / 2
        val yOffset = (bitmap.height - size) / 2
        val cropped = Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
        return Bitmap.createScaledBitmap(cropped, width, height, true)
    }
}
