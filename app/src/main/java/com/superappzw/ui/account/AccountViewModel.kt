package com.superappzw.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.UserProfileModel
import com.superappzw.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    private val _profile = MutableStateFlow<UserProfileModel?>(null)
    val profile: StateFlow<UserProfileModel?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val userService = UserService.getInstance()

    // ── Load ──────────────────────────────────────────────────────────────────

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _profile.value = userService.fetchProfile(uid = uid)
            } catch (e: Exception) {
                println("AccountViewModel: failed to load profile — ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}