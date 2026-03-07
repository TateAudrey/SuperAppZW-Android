package com.superappzw.ui.components.utils

sealed class AppAlertType {
    data class Info(
        val title: String,
        val message: String,
        val dismissAction: (() -> Unit)? = null,
    ) : AppAlertType()

    data class Confirm(
        val title: String,
        val message: String,
        val cancelAction: (() -> Unit)? = null,
        val proceedAction: (() -> Unit)? = null,
    ) : AppAlertType()

    data class SignOut(
        val title: String = "Sign Out",
        val message: String = "Are you sure you want to sign out?",
        val signOutAction: (() -> Unit)? = null,
    ) : AppAlertType()

    data class DeleteAccount(
        val title: String = "Delete Account",
        val message: String = "Are you sure you want to delete your account? This action cannot be undone.",
        val deleteAction: (() -> Unit)? = null,
    ) : AppAlertType()

    data class DeleteListing(
        val title: String,
        val cancelAction: (() -> Unit)? = null,
        val deleteAction: (() -> Unit)? = null,
    ) : AppAlertType()
}
