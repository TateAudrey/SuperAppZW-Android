package com.superappzw.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.DailyLanguageModel
import com.superappzw.services.DailyLanguageService
import com.superappzw.services.UserService
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
                _currentUserPhotoUrl.value = user.photoUrl?.toString()
                viewModelScope.launch {
                    fetchDailyLanguage()
                    fetchUserName(user.uid) // ← fetch from Firestore
                }
            } else {
                _authState.value = AuthState.Unauthenticated
                _currentUserName.value = null
                _currentUserPhotoUrl.value = null
                _dailyLanguage.value = null
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    private suspend fun fetchUserName(uid: String) {
        try {
            val profile = UserService.getInstance().fetchProfile(uid)
            _currentUserName.value = profile.firstName.ifBlank { null }
            // Also update photo URL from Firestore in case it differs from Auth
            if (profile.profileImageURL != null) {
                _currentUserPhotoUrl.value = profile.profileImageURL
            }
        } catch (e: Exception) {
            // Fall back to Firebase Auth displayName if Firestore fetch fails
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
