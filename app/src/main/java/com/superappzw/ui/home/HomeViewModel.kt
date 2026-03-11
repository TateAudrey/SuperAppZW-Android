package com.superappzw.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.superappzw.services.ListingService
import com.superappzw.ui.store.StoreListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {

    private val _allListings = MutableStateFlow<List<StoreListing>>(emptyList())
    val allListings: StateFlow<List<StoreListing>> = _allListings.asStateFlow()

    private val _isLoadingListings = MutableStateFlow(false)
    val isLoadingListings: StateFlow<Boolean> = _isLoadingListings.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val listingService = ListingService()
    private val db = FirebaseFirestore.getInstance()
    private var hasLoaded = false

    // ── Load once (seed from cache if available) ──────────────────────────────

    fun loadFeaturedListings(province: String? = null) {
        if (hasLoaded) return
        viewModelScope.launch { fetch(province) }
    }

    // ── Force reload (pull-to-refresh or province change) ─────────────────────

    fun refreshListings(province: String? = null) {
        viewModelScope.launch { fetch(province) }
    }

    // ── Private fetch ─────────────────────────────────────────────────────────

    private suspend fun fetch(province: String? = null) {
        _isLoadingListings.value = true
        _errorMessage.value = null

        try {
            val snapshot = db.collection("listings").get().await()

            val allListingsArray = mutableListOf<StoreListing>()

            for (document in snapshot.documents) {
                val userId = document.id
                val userListings = listingService.fetchUserListings(userId)
                allListingsArray.addAll(userListings)
            }

            // Filter by province if one is selected
            val filtered = if (!province.isNullOrBlank()) {
                allListingsArray.filter { listing ->
                    listing.location.trim().equals(province.trim(), ignoreCase = true)
                }
            } else {
                allListingsArray
            }

            // Sort by viewCount descending, take top 8
            _allListings.value = filtered
                .sortedByDescending { it.viewCount }
                .take(8)

            hasLoaded = true

        } catch (e: Exception) {
            _errorMessage.value = e.message
        } finally {
            _isLoadingListings.value = false
        }
    }
}