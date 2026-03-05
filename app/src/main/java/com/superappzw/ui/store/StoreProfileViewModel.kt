package com.superappzw.ui.store

import com.superappzw.model.UserProfileModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.services.ListingService
import com.superappzw.services.UserService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreProfileViewModel : ViewModel() {

    // ── Profile ───────────────────────────────────────────────────────────────

    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    private val _ownerName = MutableStateFlow("")
    val ownerName: StateFlow<String> = _ownerName.asStateFlow()

    private val _suburb = MutableStateFlow("")
    val suburb: StateFlow<String> = _suburb.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _profileImageURL = MutableStateFlow<String?>(null)
    val profileImageURL: StateFlow<String?> = _profileImageURL.asStateFlow()

    private val _ownerUID = MutableStateFlow("")
    val ownerUID: StateFlow<String> = _ownerUID.asStateFlow()

    // ── Listings ──────────────────────────────────────────────────────────────

    private val _products = MutableStateFlow<List<StoreListing>>(emptyList())
    val products: StateFlow<List<StoreListing>> = _products.asStateFlow()

    private val _services = MutableStateFlow<List<String>>(emptyList())
    val services: StateFlow<List<String>> = _services.asStateFlow()

    // ── UI State ──────────────────────────────────────────────────────────────

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val userService = UserService.getInstance()
    private val listingService = ListingService()

    // ── Load ──────────────────────────────────────────────────────────────────

    fun load(storeID: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Step 1: Fetch the user profile using the storeID as the uid.
                val profile: UserProfileModel = userService.fetchProfile(uid = storeID)

                _ownerUID.value = storeID
                _storeName.value = profile.virtualShopName.ifBlank { profile.fullName }
                _ownerName.value = profile.fullName
                _suburb.value = profile.suburb
                _location.value = profile.location
                _profileImageURL.value = profile.profileImageURL

                // Step 2: Fetch products and services in parallel — mirrors Swift's async let.
                val productsDeferred = async { listingService.fetchUserListings(storeID) }
                val servicesDeferred = async { listingService.fetchUserServices(storeID) }

                _products.value = productsDeferred.await()
                _services.value = servicesDeferred.await()

            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}