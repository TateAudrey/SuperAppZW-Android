package com.superappzw.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.UserProfileModel
import com.superappzw.services.UserService
import com.superappzw.ui.components.utils.AppAlertType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    private val _profile = MutableStateFlow<UserProfileModel?>(null)
    val profile: StateFlow<UserProfileModel?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _alertType = MutableStateFlow<AppAlertType?>(null)
    val alertType: StateFlow<AppAlertType?> = _alertType.asStateFlow()

    private val userService = UserService.getInstance()

    // ── Load — only show spinner on true first load ───────────────────────────
    // Mirrors Swift: seeds from cache instantly, then fetches fresh in background

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val showSpinner = _profile.value == null

        viewModelScope.launch {
            if (showSpinner) _isLoading.value = true
            try {
                _profile.value = userService.fetchProfile(uid = uid)
            } catch (e: Exception) {
                if (_profile.value == null) _errorMessage.value = e.message
                println("AccountViewModel: load error — ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Alert ─────────────────────────────────────────────────────────────────

    fun setAlertType(alert: AppAlertType) { _alertType.value = alert }
    fun dismissAlert() { _alertType.value = null }

    // ── Sign out ──────────────────────────────────────────────────────────────

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}