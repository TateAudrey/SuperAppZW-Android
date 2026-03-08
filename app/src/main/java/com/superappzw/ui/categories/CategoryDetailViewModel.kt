package com.superappzw.ui.categories

import com.superappzw.services.ListingService
import com.superappzw.ui.store.StoreListing
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val listingService = ListingService()

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _listings.value = listingService.fetchListingsByCategory(
                    categoryName = category.name,
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
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