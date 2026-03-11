package com.superappzw.ui.account.packages

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.services.CurrentPackageInfo
import com.superappzw.services.PackageActivationResult
import com.superappzw.services.PackageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PackageStatusViewModel : ViewModel() {

    private val _currentPackage = MutableStateFlow<CurrentPackageInfo?>(null)
    val currentPackage: StateFlow<CurrentPackageInfo?> = _currentPackage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isActivating = MutableStateFlow(false)
    val isActivating: StateFlow<Boolean> = _isActivating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _activationSuccess = MutableStateFlow<PackageActivationResult?>(null)
    val activationSuccess: StateFlow<PackageActivationResult?> = _activationSuccess.asStateFlow()

    private val _expiryBadgeColor = MutableStateFlow(Color.Transparent)
    val expiryBadgeColor: StateFlow<Color> = _expiryBadgeColor.asStateFlow()

    private val _expiryLabel = MutableStateFlow("")
    val expiryLabel: StateFlow<String> = _expiryLabel.asStateFlow()

    private val service = PackageService.shared

    // ── Load current package ──────────────────────────────────────────────────

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val info = service.fetchCurrentPackageInfo()
                _currentPackage.value = info
                updateComputedValues(info)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Activate package ──────────────────────────────────────────────────────

    fun activate(packageID: String) {
        viewModelScope.launch {
            _isActivating.value = true
            _errorMessage.value = null
            try {
                val result = service.activatePackage(packageID)
                _activationSuccess.value = result
                load() // reload to reflect new package in UI
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isActivating.value = false
            }
        }
    }

    // ── Update computed values after load ─────────────────────────────────────

    private fun updateComputedValues(info: CurrentPackageInfo?) {
        if (info == null) return

        val days = info.daysRemaining

        _expiryBadgeColor.value = when {
            days == null -> Color.Transparent
            days <= 2    -> Color(0xFFFF3B30) // red
            days <= 7    -> Color(0xFFFF9500) // orange
            else         -> Color(0xFF34C759) // green
        }

        _expiryLabel.value = when {
            info.isStandard -> "No expiry"
            days == null    -> ""
            days < 0        -> "Expired"
            days == 0       -> "Expires today"
            days == 1       -> "Expires tomorrow"
            else            -> "Expires in $days days"
        }
    }

    fun dismissError()   { _errorMessage.value = null }
    fun dismissSuccess() { _activationSuccess.value = null }
}