package com.superappzw.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class AppTermsService private constructor() {

    companion object {
        val shared = AppTermsService()

        // Mirrors Bundle.main.infoDictionary["CFBundleShortVersionString"]
        fun currentAppVersion(context: android.content.Context): String {
            return try {
                val info = context.packageManager.getPackageInfo(context.packageName, 0)
                info.versionName ?: "1.0.0"
            } catch (e: Exception) {
                "1.0.0"
            }
        }
    }

    enum class GateStatus {
        CLEAR,
        FORCE_UPDATE,
        BANNED,
        SUSPENDED,
        UPDATED_TERMS,
    }

    private val db = FirebaseFirestore.getInstance()

    suspend fun checkGateStatus(context: android.content.Context): GateStatus {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return GateStatus.CLEAR

        return try {
            coroutineScope {
                // Mirrors Swift's async let parallel fetch
                val configDeferred = async {
                    db.collection("app_config").document("config").get().await()
                }
                val userDeferred = async {
                    db.collection("users").document(uid).get().await()
                }

                val configData = configDeferred.await().data ?: emptyMap()
                val userData   = userDeferred.await().data   ?: emptyMap()

                // 1. Force update
                val minVersion = configData["minimum_version"] as? String ?: "1.0.0"
                if (isVersionLessThan(currentAppVersion(context), minVersion)) {
                    return@coroutineScope GateStatus.FORCE_UPDATE
                }

                // 2. Account status
                val status = userData["account_status"] as? String ?: "active"
                if (status == "banned")    return@coroutineScope GateStatus.BANNED
                if (status == "suspended") return@coroutineScope GateStatus.SUSPENDED

                // 3. Terms version
                val requiredTerms = configData["terms_version"] as? String ?: TermsService.CURRENT_VERSION
                val acceptedTerms = userData["terms_accepted_version"] as? String ?: "0"
                if (acceptedTerms != requiredTerms) {
                    return@coroutineScope GateStatus.UPDATED_TERMS
                }

                GateStatus.CLEAR
            }
        } catch (e: Exception) {
            println("AppTermsService: gate check failed — ${e.message}")
            GateStatus.CLEAR
        }
    }

    // Mirrors Swift's isVersionLessThan(_:_:) semantic version compare
    private fun isVersionLessThan(v1: String, v2: String): Boolean {
        val parts1 = v1.split(".").mapNotNull { it.toIntOrNull() }
        val parts2 = v2.split(".").mapNotNull { it.toIntOrNull() }
        val maxLen = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLen) {
            val a = parts1.getOrElse(i) { 0 }
            val b = parts2.getOrElse(i) { 0 }
            if (a < b) return true
            if (a > b) return false
        }
        return false
    }
}