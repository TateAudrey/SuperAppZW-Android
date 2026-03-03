package com.superappzw.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

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

    suspend fun createUserDocument(
        uid: String,
        firstName: String,
        lastName: String,
        email: String
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
            "created_at" to FieldValue.serverTimestamp(),
            "updated_at" to FieldValue.serverTimestamp()
        )

        try {
            db.collection("users")
                .document(uid)
                .set(userData)
                .await()
        } catch (e: Exception) {
            // Handle error - log or throw as needed
            throw e
        }
    }
}
