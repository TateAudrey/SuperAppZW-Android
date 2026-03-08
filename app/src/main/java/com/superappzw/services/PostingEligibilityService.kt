package com.superappzw.services

import com.google.firebase.auth.FirebaseAuth


// ── Result ────────────────────────────────────────────────────────────────────

sealed class PostingEligibilityResult {
    object Allowed : PostingEligibilityResult()
    object ProfileIncomplete : PostingEligibilityResult()
    data class LimitReached(val message: String) : PostingEligibilityResult()
    data class Error(val message: String) : PostingEligibilityResult()
}

// ── Service ───────────────────────────────────────────────────────────────────

class PostingEligibilityService private constructor() {

    companion object {
        val shared = PostingEligibilityService()
    }

    private val userService = UserService.getInstance()

    suspend fun check(isService: Boolean): PostingEligibilityResult {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return PostingEligibilityResult.Error("No authenticated user.")

        return try {
            // Step 1 — Check profile is complete
            val profileComplete = userService.isProfileComplete(uid = uid)
            if (!profileComplete) return PostingEligibilityResult.ProfileIncomplete

            // Step 2 — Fetch post counts and package
            val (productCount, serviceCount, packageID) = userService.fetchPostCounts(uid = uid)
            val limit = userService.fetchPackageLimit(packageID = packageID)

            // Step 3 — Check against limit
            if (isService) {
                if (serviceCount >= limit.maxServices) {
                    return PostingEligibilityResult.LimitReached(
                        message = "You've reached the ${limit.name} package limit of ${limit.maxServices} services. Upgrade to post more."
                    )
                }
            } else {
                if (productCount >= limit.maxProducts) {
                    return PostingEligibilityResult.LimitReached(
                        message = "You've reached the ${limit.name} package limit of ${limit.maxProducts} products. Upgrade to post more."
                    )
                }
            }

            PostingEligibilityResult.Allowed

        } catch (e: Exception) {
            PostingEligibilityResult.Error(e.message ?: "An unknown error occurred.")
        }
    }
}