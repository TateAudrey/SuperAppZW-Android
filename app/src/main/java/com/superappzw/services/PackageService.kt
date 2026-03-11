package com.superappzw.services

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

// ── Models ────────────────────────────────────────────────────────────────────

data class PackageActivationResult(
    val packageID: String,
    val expiresAt: Date,
    val validDays: Int,
) {
    val formattedExpiry: String
        get() = DateFormat.getDateInstance(DateFormat.LONG).format(expiresAt)
}

data class CurrentPackageInfo(
    val packageID:   String,
    val packageName: String,
    val maxProducts: Int,
    val maxServices: Int,
    val validDays:   Int,
    val expiresAt:   Date?,
    val isStandard:  Boolean,
) {
    val formattedExpiry: String?
        get() = expiresAt?.let { DateFormat.getDateInstance(DateFormat.LONG).format(it) }

    val daysRemaining: Int?
        get() {
            val expiry = expiresAt ?: return null
            val today  = Calendar.getInstance()
            val end    = Calendar.getInstance().apply { time = expiry }
            val diffMs = end.timeInMillis - today.timeInMillis
            return (diffMs / (1000 * 60 * 60 * 24)).toInt()
        }
}

// ── Errors ────────────────────────────────────────────────────────────────────

sealed class PackageServiceError(message: String) : Exception(message) {
    object InvalidResponse  : PackageServiceError("Invalid response from server. Please try again.")
    object NotAuthenticated : PackageServiceError("You must be signed in to activate a package.")
}

// ── Service ───────────────────────────────────────────────────────────────────

class PackageService private constructor() {

    companion object {
        val shared = PackageService()
    }

    private val functions = FirebaseFunctions.getInstance()
    private val db        = FirebaseFirestore.getInstance()

    // ── Activate package ──────────────────────────────────────────────────────
    // Calls the Firebase Cloud Function "activatePackage" and returns the result.

    suspend fun activatePackage(packageID: String): PackageActivationResult {
        val result = functions
            .getHttpsCallable("activatePackage")
            .call(mapOf("packageID" to packageID))
            .await()

        @Suppress("UNCHECKED_CAST")
        val data = result.data as? Map<String, Any>
            ?: throw PackageServiceError.InvalidResponse

        val pid       = data["packageID"]  as? String ?: throw PackageServiceError.InvalidResponse
        val expiryStr = data["expiresAt"]  as? String ?: throw PackageServiceError.InvalidResponse
        val validDays = (data["validDays"] as? Long)?.toInt() ?: throw PackageServiceError.InvalidResponse

        val expiresAt = runCatching {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Date.from(java.time.Instant.parse(expiryStr))
            } else {
                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
                    .parse(expiryStr)
            }
        }.getOrNull() ?: throw PackageServiceError.InvalidResponse

        return PackageActivationResult(
            packageID = pid,
            expiresAt = expiresAt,
            validDays = validDays,
        )
    }

    // ── Fetch current package info ────────────────────────────────────────────

    suspend fun fetchCurrentPackageInfo(): CurrentPackageInfo? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw PackageServiceError.NotAuthenticated

        val userDoc = db.collection("users").document(uid).get().await()
        val data    = userDoc.data ?: return null

        val packageID = data["package_id"] as? String ?: "standard"
        val expiresAt = (data["package_expires_at"] as? Timestamp)?.toDate()

        val packageDoc = db.collection("packages").document(packageID).get().await()
        val pkgData    = packageDoc.data

        return CurrentPackageInfo(
            packageID   = packageID,
            packageName = pkgData?.get("name") as? String
                ?: packageID.replaceFirstChar { it.uppercase() },
            maxProducts = (pkgData?.get("max_products") as? Long)?.toInt() ?: 0,
            maxServices = (pkgData?.get("max_services") as? Long)?.toInt() ?: 0,
            validDays   = (pkgData?.get("valid_days")   as? Long)?.toInt() ?: 0,
            expiresAt   = if (packageID == "standard") null else expiresAt,
            isStandard  = packageID == "standard",
        )
    }
}