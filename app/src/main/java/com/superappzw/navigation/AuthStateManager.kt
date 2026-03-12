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

    // null  = gate check not yet run (shows PreLoadView)
    // non-null = gate check complete (shows the appropriate screen)
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
                _authState.value = AuthState.Authenticated(user.uid)
                _currentUserPhotoUrl.value = user.photoUrl?.toString()
                viewModelScope.launch {
                    fetchDailyLanguage()
                    fetchUserName(user.uid)
                    checkGate() // ← run gate immediately on auth
                }
            } else {
                _authState.value = AuthState.Unauthenticated
                _gateStatus.value = null // reset on sign-out
                _currentUserName.value = null
                _currentUserPhotoUrl.value = null
                _dailyLanguage.value = null
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    // Called after UpdatedTermsView acknowledges, and on sign-in
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
