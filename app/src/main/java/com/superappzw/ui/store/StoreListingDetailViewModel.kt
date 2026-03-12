package com.superappzw.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.services.FavouritesService
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

    // Add alongside existing StateFlows:
    private val _isFavourited = MutableStateFlow(false)
    val isFavourited: StateFlow<Boolean> = _isFavourited.asStateFlow()

    // Call this inside load() after the listing is fetched:
    private suspend fun checkFavourited(itemCode: String) {
        _isFavourited.value = FavouritesService.shared.isFavourited(itemCode)
    }

    // New public function:
    fun toggleFavourite() {
        val current = listing.value ?: return
        viewModelScope.launch {
            try {
                val isNowFavourited = FavouritesService.shared.toggle(current)
                _isFavourited.value = isNowFavourited
            } catch (e: Exception) {
                println("StoreListingDetailViewModel: toggleFavourite failed — ${e.message}")
            }
        }
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    fun load(itemCode: String, ownerUserID: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetched = listingService.fetchListing(itemCode = itemCode, ownerUserID = ownerUserID)
                _listing.value = fetched
                // Check saved state from Firestore — replaces the broken local state
                checkFavourited(itemCode)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load listing."
            } finally {
                _isLoading.value = false
            }
        }
    }
}