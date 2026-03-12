package com.superappzw.ui.accountStatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.services.TermsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TermsViewModel : ViewModel() {

    private val service = TermsService.shared

    private val _terms = MutableStateFlow<AppTerms?>(null)
    val terms: StateFlow<AppTerms?> = _terms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAccepting = MutableStateFlow(false)
    val isAccepting: StateFlow<Boolean> = _isAccepting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _didAccept = MutableStateFlow(false)
    val didAccept: StateFlow<Boolean> = _didAccept.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _terms.value = service.fetchTerms()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load terms."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun accept() {
        viewModelScope.launch {
            _isAccepting.value = true
            try {
                service.acceptTerms()
                _didAccept.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to accept terms."
            } finally {
                _isAccepting.value = false
            }
        }
    }
}