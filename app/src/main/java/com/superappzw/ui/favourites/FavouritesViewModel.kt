package com.superappzw.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.services.FavouritesService
import com.superappzw.ui.store.StoreListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FavouritesViewModel : ViewModel() {

    private val service = FavouritesService.shared

    private val _listings = MutableStateFlow<List<StoreListing>>(emptyList())
    val listings: StateFlow<List<StoreListing>> = _listings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun load() {
        println("FavouritesViewModel: load() called — vm=${this.hashCode()}")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = service.fetchAll()
                println("FavouritesViewModel: fetched ${result.size} listings — vm=${this@FavouritesViewModel.hashCode()}")
                _listings.value = result
                println("FavouritesViewModel: _listings updated — vm=${this@FavouritesViewModel.hashCode()} size=${_listings.value.size}")
                _errorMessage.value = null
            } catch (e: Exception) {
                println("FavouritesViewModel: EXCEPTION — ${e.message}")
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun remove(listing: StoreListing) {
        _listings.value = _listings.value.filter { it.itemCode != listing.itemCode }
        viewModelScope.launch {
            runCatching { service.remove(listing.itemCode) }
        }
    }
}