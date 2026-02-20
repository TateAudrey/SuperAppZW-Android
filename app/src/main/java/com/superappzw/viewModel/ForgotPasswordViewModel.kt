package com.superappzw.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.superappzw.ui.components.utils.AppAlertType

class ForgotPasswordViewModel : ViewModel() {
    // Form Fields
    var email by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmNewPassword by mutableStateOf("")

    // UI State
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    var alertType by mutableStateOf<AppAlertType?>(null)

    // Request Password Reset Email
    fun requestPasswordReset() {}
}
