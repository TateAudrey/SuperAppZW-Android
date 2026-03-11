package com.superappzw.model

data class UserProfileModel(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val emailAddress: String = "",
    val suburb: String = "",
    val location: String = "",
    val virtualShopName: String = "",
    val profileImageURL: String? = null,
    val packageID: String = "standard",
    val isProfileComplete: Boolean = false,
) {
    // ── Computed properties ───────────────────────────────────────────────────

    val fullName: String
        get() = "$firstName $lastName".trim()

    val initials: String
        get() {
            val f = firstName.take(1).uppercase()
            val l = lastName.take(1).uppercase()
            return if (f.isEmpty()) "?" else "$f$l"
        }

    // ── Firestore mapping ─────────────────────────────────────────────────────

    companion object {
        fun fromData(data: Map<String, Any>): UserProfileModel {
            return UserProfileModel(
                firstName = data["first_name"] as? String ?: "",
                lastName = data["last_name"] as? String ?: "",
                phoneNumber = data["phone_number"] as? String ?: "",
                emailAddress = data["email"] as? String ?: "",
                suburb = data["suburb"] as? String ?: "",
                location = data["location"] as? String ?: "",
                virtualShopName = data["virtual_shop_name"] as? String ?: "",
                profileImageURL = data["profile_image_url"] as? String,
                packageID = (data["package_id"] as? String ?: "standard").lowercase(),
                isProfileComplete = data["is_profile_complete"] as? Boolean ?: false,
            )
        }
    }
}