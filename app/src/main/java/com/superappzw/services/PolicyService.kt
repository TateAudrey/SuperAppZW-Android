package com.superappzw.services

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// ── Model ─────────────────────────────────────────────────────────────────────

data class AppPolicy(
    val lastUpdated: String,
    val html: String,
)

// ── Service ───────────────────────────────────────────────────────────────────

class PolicyService private constructor() {

    companion object {
        val shared = PolicyService()
    }

    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchPolicy(): AppPolicy {
        val doc = db
            .collection("policies")
            .document("app_policies")
            .get()
            .await()

        val data = doc.data
            ?: throw Exception("Policy document not found.")

        return AppPolicy(
            lastUpdated = data["last_updated"] as? String ?: "",
            html        = data["html"]         as? String ?: "",
        )
    }
}