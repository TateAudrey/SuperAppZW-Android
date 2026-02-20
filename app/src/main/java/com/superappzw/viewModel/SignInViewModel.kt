package com.superappzw.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.superappzw.ui.components.utils.AppAlertType

class SignInViewModel : ViewModel() {
    // Form fields
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // UI State
    var isLoading by mutableStateOf(false)
    var alertType by mutableStateOf<AppAlertType?>(null)

    // Sign In
    fun signIn() {
    }
}
