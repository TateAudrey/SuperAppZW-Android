package com.superappzw.viewModel

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.services.AuthService
import com.superappzw.services.FirebaseErrorMapper
import com.superappzw.services.UserService
import com.superappzw.ui.components.utils.AppAlertType
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    // Form fields
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    // UI State
    var isLoading by mutableStateOf(false)
    var alertType by mutableStateOf<AppAlertType?>(null)

    fun signUp() {
        // Clear previous alerts
        alertType = null

        // MARK: - Validation 1: All fields required
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
            password.isBlank() || confirmPassword.isBlank()) {
            alertType = AppAlertType.Info(
                title = "Missing Information",
                message = "Please fill in all fields.",
                dismissAction = {}
            )
            return
        }

        // MARK: - Validation 2: Passwords match
        if (password != confirmPassword) {
            alertType = AppAlertType.Info(
                title = "Invalid Password",
                message = "Passwords do not match.",
                dismissAction = {}
            )
            return
        }

        // MARK: - Validation 3: Password length
        if (password.length < 6) {
            alertType = AppAlertType.Info(
                title = "Weak Password",
                message = "Password must be at least 6 characters.",
                dismissAction = {}
            )
            return
        }

        // MARK: - Execute signup
        isLoading = true

        viewModelScope.launch {
            try {
                // 1. Create Firebase Auth user
                val user = AuthService.getInstance().createUser(email, password)
                if (user != null) {
                    // 2. Create Firestore user document
                    UserService.getInstance().createUserDocument(
                        uid = user.uid,
                        firstName = firstName,
                        lastName = lastName,
                        email = email
                    )

                    // Success (Firebase listener will handle navigation)
                    alertType = AppAlertType.Info(
                        title = "Success!",
                        message = "Account created successfully!",
                        dismissAction = { }
                    )
                } else {
                    throw Exception("Failed to create user")
                }
            } catch (e: Exception) {
                // Handle Firebase errors with user-friendly messages
                val message = FirebaseErrorMapper.mapError(e)
                alertType = AppAlertType.Info(
                    title = "Sign Up Failed",
                    message = message,
                    dismissAction = { }
                )
            } finally {
                isLoading = false
            }
        }
    }
}
