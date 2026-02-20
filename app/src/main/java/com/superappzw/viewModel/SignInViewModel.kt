package com.superappzw.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.superappzw.ui.components.utils.AppAlertType
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.services.FirebaseErrorMapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel : ViewModel() {
    // Form fields
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // UI State
    var isLoading by mutableStateOf(false)
    var alertType by mutableStateOf<AppAlertType?>(null)

    // Sign In
    fun signIn() {
        viewModelScope.launch {
            try {
                isLoading = true
                alertType = null

                // Validate inputs
                if (email.isBlank() || password.isBlank()) {
                    alertType = AppAlertType.Info(
                        title = "Missing Fields",
                        message = "Please fill all fields"
                    )
                    return@launch
                }

                // Firebase sign in
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password).await()

                // Success → AuthStateManager handles navigation
                alertType = AppAlertType.Info(
                    title = "Welcome Back!",
                    message = "Signed in successfully"
                )

            } catch (e: Exception) {
                // Use YOUR FirebaseErrorMapper
                val errorMessage = FirebaseErrorMapper.mapError(e)
                alertType = AppAlertType.Info(
                    title = "Sign In Failed",
                    message = errorMessage
                )
            } finally {
                isLoading = false
            }
        }
    }
}

