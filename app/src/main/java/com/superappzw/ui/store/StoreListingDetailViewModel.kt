package com.superappzw.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.services.ListingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreListingDetailViewModel : ViewModel() {

    private val _listing = MutableStateFlow<StoreListing?>(null)
    val listing: StateFlow<StoreListing?> = _listing.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val listingService = ListingService()

    // ── Load ──────────────────────────────────────────────────────────────────

    fun load(itemCode: String, ownerUserID: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val listings = listingService.fetchUserListings(ownerUserID)
                _listing.value = listings.firstOrNull { it.itemCode == itemCode }
                if (_listing.value == null) {
                    _errorMessage.value = "Listing not found."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}