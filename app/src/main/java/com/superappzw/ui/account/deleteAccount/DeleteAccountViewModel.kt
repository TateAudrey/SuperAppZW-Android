package com.superappzw.ui.account.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.superappzw.services.AccountDeletionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeleteAccountViewModel : ViewModel() {

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Firebase requires recent authentication for sensitive operations.
    // If the user signed in a long time ago, deletion will fail with
    // requiresRecentLogin — we surface a re-auth prompt in that case.
    private val _requiresReauth = MutableStateFlow(false)
    val requiresReauth: StateFlow<Boolean> = _requiresReauth.asStateFlow()

    private val deletionService = AccountDeletionService.shared

    fun deleteAccount() {
        viewModelScope.launch {
            _isDeleting.value = true
            _errorMessage.value = null

            try {
                deletionService.deleteAccount(wasBanned = false)
                _isDeleted.value = true
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                // Mirrors Swift: error.code == AuthErrorCode.requiresRecentLogin
                _requiresReauth.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun dismissError() { _errorMessage.value = null }
    fun dismissReauth() { _requiresReauth.value = false }
}