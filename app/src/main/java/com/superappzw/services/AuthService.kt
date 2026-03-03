package com.superappzw.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthService private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: AuthService? = null

        fun getInstance(): AuthService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthService().also { INSTANCE = it }
            }
        }
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // MARK: - Create User
    suspend fun createUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    // MARK: - Get Current User
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
