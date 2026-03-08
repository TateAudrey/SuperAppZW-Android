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

    // ── Load once ─────────────────────────────────────────────────────────────

    fun loadFeaturedListings() {
        if (hasLoaded) return
        viewModelScope.launch { fetchAllListings() }
    }

    // ── Force reload (pull to refresh) ────────────────────────────────────────

    fun refreshListings() {
        viewModelScope.launch { fetchAllListings() }
    }

    // ── Private fetch ─────────────────────────────────────────────────────────

    private suspend fun fetchAllListings() {
        _isLoadingListings.value = true
        _errorMessage.value = null

        try {
            // Get all user documents from listings collection
            val snapshot = db.collection("listings").get().await()

            val allListingsArray = mutableListOf<StoreListing>()

            // Fetch listings for each user document
            for (document in snapshot.documents) {
                val userId = document.id
                val userListings = listingService.fetchUserListings(userId)
                allListingsArray.addAll(userListings)
            }

            // Sort by viewCount descending, take top 8
            _allListings.value = allListingsArray
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