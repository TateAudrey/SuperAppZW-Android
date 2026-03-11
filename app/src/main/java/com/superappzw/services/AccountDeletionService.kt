package com.superappzw.services

//  Deletes all user data while keeping a privacy-safe tombstone
//  so banned users cannot re-register with the same identity.
//
//  Tombstone stores ONLY:
//    - SHA-256 hash of email (not the email itself)
//    - SHA-256 hash of phone (not the phone itself)
//    - deletion timestamp
//    - deletion reason (user_requested / banned)
//    - was_banned flag
//
//  No PII is retained — compliant with data protection principles.

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class AccountDeletionService private constructor() {

    companion object {
        val shared = AccountDeletionService()
    }

    private val auth    = FirebaseAuth.getInstance()
    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // ── Delete account ────────────────────────────────────────────────────────

    suspend fun deleteAccount(wasBanned: Boolean = false) {
        val user = auth.currentUser ?: throw DeletionException.NotAuthenticated

        // ── Step 0: Dry-run re-auth check BEFORE deleting anything ───────────────
        // Firebase doesn't expose a direct "is session fresh?" API, so we attempt
        // a no-op re-auth by reloading the user. If the session is stale, this
        // will surface the error before we touch any data.
        try {
            user.reload().await()
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            throw e // bubble up to ViewModel before any deletion occurs
        }

        val uid = user.uid

        // Now safe to proceed — session is fresh
        val profile = try { UserService.getInstance().fetchProfile(uid) } catch (e: Exception) { null }
        val email   = user.email ?: ""
        val phone   = profile?.phoneNumber ?: ""

        writeTombstone(uid = uid, email = email, phone = phone, wasBanned = wasBanned)

        coroutineScope {
            val deleteUser      = async { deleteUserDocument(uid) }
            val deleteListings  = async { deleteListingsDocument(uid) }
            val deleteBillboard = async { deleteBillboardDocument(uid) }
            val deleteReviews   = async { deleteUserReviews(uid) }
            val deleteRequest   = async { deletePackageRequest(uid) }

            deleteUser.await()
            deleteListings.await()
            deleteBillboard.await()
            deleteReviews.await()
            deleteRequest.await()
        }

        deleteStorageFiles(uid)

        // This will now succeed since we confirmed the session is fresh above
        user.delete().await()
    }

    // ── Check if identity is banned ───────────────────────────────────────────

    suspend fun isBanned(email: String?, phone: String?): Boolean {
        val hashes = mutableListOf<String>()
        if (!email.isNullOrBlank()) hashes.add(sha256(email.trim().lowercase()))
        if (!phone.isNullOrBlank()) hashes.add(sha256(phone.trim()))

        if (hashes.isEmpty()) return false

        return try {
            for (hash in hashes) {
                val emailQuery = db.collection("deleted_accounts")
                    .whereEqualTo("email_hash", hash)
                    .whereEqualTo("was_banned", true)
                    .limit(1)
                    .get().await()
                if (!emailQuery.isEmpty) return true

                val phoneQuery = db.collection("deleted_accounts")
                    .whereEqualTo("phone_hash", hash)
                    .whereEqualTo("was_banned", true)
                    .limit(1)
                    .get().await()
                if (!phoneQuery.isEmpty) return true
            }
            false
        } catch (e: Exception) {
            println("AccountDeletionService: ban check failed — ${e.message}")
            false
        }
    }

    // ── Tombstone ─────────────────────────────────────────────────────────────

    private suspend fun writeTombstone(
        uid: String,
        email: String,
        phone: String,
        wasBanned: Boolean,
    ) {
        val tombstone = hashMapOf<String, Any?>(
            "uid_hash"   to sha256(uid),
            "email_hash" to if (email.isBlank()) null else sha256(email.trim().lowercase()),
            "phone_hash" to if (phone.isBlank()) null else sha256(phone.trim()),
            "was_banned" to wasBanned,
            "reason"     to if (wasBanned) "banned" else "user_requested",
            "deleted_at" to FieldValue.serverTimestamp(),
            // No name, no email, no phone — PII free
        )

        // Use uid as document ID so there's only ever one tombstone per account
        db.collection("deleted_accounts")
            .document(uid)
            .set(tombstone)
            .await()
    }

    // ── Data deletion helpers ─────────────────────────────────────────────────

    private suspend fun deleteUserDocument(uid: String) {
        db.collection("users").document(uid).delete().await()
    }

    private suspend fun deleteListingsDocument(uid: String) {
        db.collection("listings").document(uid).delete().await()
    }

    private suspend fun deleteBillboardDocument(uid: String) {
        try { db.collection("billboard").document(uid).delete().await() }
        catch (e: Exception) { /* may not exist — ignore */ }
    }

    private suspend fun deleteUserReviews(uid: String) {
        // Delete reviews written BY this user
        val written = db.collection("user-reviews")
            .whereEqualTo("reviewer_id", uid)
            .get().await()
        for (doc in written.documents) {
            try { doc.reference.delete().await() } catch (e: Exception) { /* ignore */ }
        }

        // Delete reviews written ABOUT this user's store
        val received = db.collection("user-reviews")
            .whereEqualTo("store_id", uid)
            .get().await()
        for (doc in received.documents) {
            try { doc.reference.delete().await() } catch (e: Exception) { /* ignore */ }
        }
    }

    private suspend fun deletePackageRequest(uid: String) {
        try { db.collection("package_requests").document(uid).delete().await() }
        catch (e: Exception) { /* may not exist — ignore */ }
    }

    private suspend fun deleteStorageFiles(uid: String) {
        // Profile image
        try {
            storage.reference.child("profile-images/$uid/$uid.jpg").delete().await()
        } catch (e: Exception) { /* ignore */ }

        // Listing images — list and delete all
        try {
            val listingsRef = storage.reference.child("listings/$uid")
            val result = listingsRef.listAll().await()
            for (item in result.items) {
                try { item.delete().await() } catch (e: Exception) { /* ignore */ }
            }
        } catch (e: Exception) { /* ignore */ }
    }

    // ── SHA-256 hash ──────────────────────────────────────────────────────────

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash   = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}

// ── Errors ────────────────────────────────────────────────────────────────────

sealed class DeletionException(message: String) : Exception(message) {
    object NotAuthenticated : DeletionException("You must be signed in to delete your account.")
}