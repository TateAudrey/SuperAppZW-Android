package com.superappzw.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.DailyLanguageModel
import com.superappzw.services.AppTermsService
import com.superappzw.services.DailyLanguageService
import com.superappzw.services.NetworkMonitor
import com.superappzw.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthStateManager(private val application: Application) : AndroidViewModel(application) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _gateStatus = MutableStateFlow<AppTermsService.GateStatus?>(null)
    val gateStatus: StateFlow<AppTermsService.GateStatus?> = _gateStatus.asStateFlow()

    private val _dailyLanguage = MutableStateFlow<DailyLanguageModel?>(null)
    val dailyLanguage: StateFlow<DailyLanguageModel?> = _dailyLanguage.asStateFlow()

    private val _dailyLanguageError = MutableStateFlow<String?>(null)
    val dailyLanguageError: StateFlow<String?> = _dailyLanguageError.asStateFlow()

    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> = _currentUserName.asStateFlow()

    private val _currentUserPhotoUrl = MutableStateFlow<String?>(null)
    val currentUserPhotoUrl: StateFlow<String?> = _currentUserPhotoUrl.asStateFlow()

    // ── Guest state ───────────────────────────────────────────────────────────

    private val _isGuest = MutableStateFlow(false)
    val isGuest: StateFlow<Boolean> = _isGuest.asStateFlow()

    fun continueAsGuest() {
        _isGuest.value = true
    }

    fun exitGuestMode() {
        _isGuest.value = false
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private val auth = FirebaseAuth.getInstance()
    private val languageService = DailyLanguageService()
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    val networkMonitor: NetworkMonitor
        get() = NetworkMonitor.getInstance(application)

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                _isGuest.value = false  // ← clear guest mode on sign in
                _authState.value = AuthState.Authenticated(user.uid)
                _currentUserPhotoUrl.value = user.photoUrl?.toString()
                viewModelScope.launch {
                    fetchDailyLanguage()
                    fetchUserName(user.uid)
                    checkGate()
                }
            } else {
                _isGuest.value = false  // ← clear guest mode on sign out
                _authState.value = AuthState.Unauthenticated
                _gateStatus.value = null
                _currentUserName.value = null
                _currentUserPhotoUrl.value = null
                _dailyLanguage.value = null
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    fun checkGate() {
        viewModelScope.launch {
            _gateStatus.value = AppTermsService.shared.checkGateStatus(application)
        }
    }

    private suspend fun fetchUserName(uid: String) {
        try {
            val profile = UserService.getInstance().fetchProfile(uid)
            _currentUserName.value = profile.firstName.ifBlank { null }
            if (profile.profileImageURL != null) {
                _currentUserPhotoUrl.value = profile.profileImageURL
            }
        } catch (e: Exception) {
            _currentUserName.value = FirebaseAuth.getInstance().currentUser?.displayName
        }
    }

    fun logout() {
        auth.signOut()
    }

    private suspend fun fetchDailyLanguage() {
        try {
            _dailyLanguage.value = languageService.fetchTodaysLanguage()
        } catch (e: Exception) {
            _dailyLanguageError.value = e.message
        }
    }

    override fun onCleared() {
        super.onCleared()
        authStateListener?.let { auth.removeAuthStateListener(it) }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val uid: String) : AuthState()
}
