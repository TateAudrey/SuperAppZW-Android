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

    private val _alertType = MutableStateFlow<AppAlertType?>(null)
    val alertType: StateFlow<AppAlertType?> = _alertType.asStateFlow()

    private val userService = UserService.getInstance()

    // ── Load ──────────────────────────────────────────────────────────────────

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _profile.value = userService.fetchProfile(uid = uid)
            } catch (e: Exception) {
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

    // ── Delete account ────────────────────────────────────────────────────────

//    fun deleteAccount() {
//        val user = FirebaseAuth.getInstance().currentUser ?: return
//
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                // Step 1 — Delete Firestore user document
//                userService.deleteUserDocument(uid = user.uid)
//                // Step 2 — Delete Firebase Auth account
//                user.delete()
//            } catch (e: Exception) {
//                println("AccountViewModel: deleteAccount error — ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
}