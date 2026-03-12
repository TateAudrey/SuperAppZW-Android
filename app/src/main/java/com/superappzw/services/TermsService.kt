package com.superappzw.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.superappzw.ui.accountStatus.AppTerms
import kotlinx.coroutines.tasks.await

class TermsService private constructor() {

    companion object {
        val shared = TermsService()
        const val CURRENT_VERSION = "1"
    }

    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchTerms(): AppTerms {
        val doc = db.collection("terms")
            .document("app_terms")
            .get()
            .await()

        val data = doc.data
            ?: throw Exception("Terms document not found.")

        return AppTerms(
            lastUpdated = data["last_updated"] as? String ?: "",
            html        = data["html"]         as? String ?: "",
            version     = data["version"]      as? String ?: CURRENT_VERSION,
        )
    }

    suspend fun acceptTerms() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid).update(
            mapOf(
                "terms_accepted_at"      to FieldValue.serverTimestamp(),
                "terms_accepted_version" to CURRENT_VERSION,  // ← already a String const, this is fine
                "did_accept_terms"       to true,
            )
        ).await()
    }
}