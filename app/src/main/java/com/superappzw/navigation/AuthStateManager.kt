package com.superappzw.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.DailyLanguageModel
import com.superappzw.services.DailyLanguageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthStateManager : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _dailyLanguage = MutableStateFlow<DailyLanguageModel?>(null)
    val dailyLanguage: StateFlow<DailyLanguageModel?> = _dailyLanguage.asStateFlow()

    private val _dailyLanguageError = MutableStateFlow<String?>(null)
    val dailyLanguageError: StateFlow<String?> = _dailyLanguageError.asStateFlow()

    // ── Current user ──────────────────────────────────────────────────────────
    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> = _currentUserName.asStateFlow()

    private val _currentUserPhotoUrl = MutableStateFlow<String?>(null)
    val currentUserPhotoUrl: StateFlow<String?> = _currentUserPhotoUrl.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val languageService = DailyLanguageService()
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                _authState.value = AuthState.Authenticated(user.uid)
                _currentUserName.value = user.displayName
                _currentUserPhotoUrl.value = user.photoUrl?.toString()
                viewModelScope.launch { fetchDailyLanguage() }
            } else {
                _authState.value = AuthState.Unauthenticated
                _dailyLanguage.value = null
                _dailyLanguageError.value = null
                _currentUserName.value = null
                _currentUserPhotoUrl.value = null
            }
        }
        auth.addAuthStateListener(authStateListener!!)
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
