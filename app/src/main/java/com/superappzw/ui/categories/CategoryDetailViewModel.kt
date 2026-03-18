package com.superappzw.ui.categories

import com.superappzw.services.ListingService
import com.superappzw.ui.store.StoreListing
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryDetailViewModel(
    private val category: CategoryItem,
) : ViewModel() {

    private val _listings = MutableStateFlow<List<StoreListing>>(emptyList())
    val listings: StateFlow<List<StoreListing>> = _listings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val listingService = ListingService()
    private var lastDocument: DocumentSnapshot? = null

    // ── Initial load / refresh ────────────────────────────────────────────────

    fun load() {
        viewModelScope.launch {
            resetPagination()
            fetchNextPage(showSpinner = true)
        }
    }

    // ── Load more (called when user scrolls to bottom) ────────────────────────

    fun loadMoreIfNeeded() {
        if (!_hasMore.value || _isLoadingMore.value || _isLoading.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            fetchNextPage(showSpinner = false)
            _isLoadingMore.value = false
        }
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private fun resetPagination() {
        _listings.value = emptyList()
        lastDocument = null
        _hasMore.value = true
        _errorMessage.value = null
    }

    private suspend fun fetchNextPage(showSpinner: Boolean) {
        if (showSpinner) _isLoading.value = true
        _errorMessage.value = null

        try {
            val result = listingService.fetchListingsByCategoryPage(
                categoryName = category.name,
                after = lastDocument,
            )
            _listings.value = _listings.value + result.listings
            lastDocument = result.lastDocument
            _hasMore.value = result.hasMore
        } catch (e: Exception) {
            if (_listings.value.isEmpty()) {
                _errorMessage.value = e.message
            }
        } finally {
            _isLoading.value = false
        }
    }
}

// ── Factory ───────────────────────────────────────────────────────────────────

class CategoryDetailViewModelFactory(
    private val category: CategoryItem,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CategoryDetailViewModel(category) as T
    }
}