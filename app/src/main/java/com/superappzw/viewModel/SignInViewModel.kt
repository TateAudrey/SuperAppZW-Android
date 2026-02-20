package com.superappzw.viewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.superappzw.ui.components.utils.AppAlertType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.superappzw.services.FirebaseErrorMapper
import com.superappzw.services.UserService
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel : ViewModel() {
    // Form fields
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // UI State
    var isLoading by mutableStateOf(false)
    var alertType by mutableStateOf<AppAlertType?>(null)

    private val auth = FirebaseAuth.getInstance()

    // Email Sign In
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

    fun signInWithGoogle(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                val credentialManager = CredentialManager.create(context)

                // 1. Configure Google ID Option
                val googleIdOption =
                    GetCredentialRequest.Builder()
                    .addCredentialOption(
                        GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false) // Show all accounts
                        .setServerClientId("229725757226-o33rrm3cchbkfvroo2rmm4bael9m840q.apps.googleusercontent.com")
                        .build()
                    )
                    .build()

                // 2. Launch the selector
                val result = credentialManager.getCredential(context, googleIdOption)
                val credential = result.credential

                // 3. Extract ID Token and Sign into Firebase
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    val authResult = auth.signInWithCredential(firebaseCredential).await()
                    val user = authResult.user

                    // 4. If this is a NEW user, create their document in Firestore
                    if (authResult.additionalUserInfo?.isNewUser == true && user != null) {
                        // Split display name if available, otherwise use email part
                        val displayName = user.displayName ?: ""
                        val names = displayName.split(" ")
                        val firstName = names.getOrNull(0) ?: user.email?.split("@")?.getOrNull(0) ?: "User"
                        val lastName = names.getOrNull(1) ?: ""

                        UserService.getInstance().createUserDocument(
                            uid = user.uid,
                            firstName = firstName,
                            lastName = lastName,
                            email = user.email ?: ""
                        )
                    }

                    onResult(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMessage = FirebaseErrorMapper.mapError(e)
                alertType = AppAlertType.Info(
                    title = "Google Sign In Failed",
                    message = errorMessage
                )
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }
}
