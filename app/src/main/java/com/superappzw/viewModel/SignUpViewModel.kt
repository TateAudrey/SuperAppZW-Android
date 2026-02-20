package com.superappzw.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.superappzw.ui.components.utils.AppAlertType

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
}
