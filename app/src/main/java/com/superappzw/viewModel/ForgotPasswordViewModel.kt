package com.superappzw.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.superappzw.ui.components.utils.AppAlertType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.services.FirebaseErrorMapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ForgotPasswordViewModel : ViewModel() {
    // Form Fields - SIMPLIFIED (email only for reset link)
    var email by mutableStateOf("")

    // UI State - UNIFIED (use your AppAlertType system)
    var isLoading by mutableStateOf(false)
    var alertType by mutableStateOf<AppAlertType?>(null)

    // Request Password Reset Email
    fun requestPasswordReset(
        navigateToSignIn: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                alertType = null

                // Validate email
                if (email.isBlank()) {
                    alertType = AppAlertType.Info(
                        title = "Missing Email",
                        message = "Please enter your email address"
                    )
                    return@launch
                }

                // Send reset email
                val auth = FirebaseAuth.getInstance()
                auth.sendPasswordResetEmail(email).await()

                // Success - show instructions
                alertType = AppAlertType.Info(
                    title = "Reset Email Sent!",
                    message = "Check your email for password reset instructions. Note: It may take a few minutes to arrive.",
                    dismissAction = navigateToSignIn
                )

            } catch (e: Exception) {
                // Use YOUR FirebaseErrorMapper
                val errorMessage = FirebaseErrorMapper.mapError(e)
                alertType = AppAlertType.Info(
                    title = "Reset Failed",
                    message = errorMessage
                )
            } finally {
                isLoading = false
            }
        }
    }
}

