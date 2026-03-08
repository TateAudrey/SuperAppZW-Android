package com.superappzw.ui.home.billboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.superappzw.services.BillboardService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillboardViewModel(
    private val service: BillboardService = BillboardService(),
) : ViewModel() {

    private val _items = MutableStateFlow<List<BillboardItemModel>>(emptyList())
    val items: StateFlow<List<BillboardItemModel>> = _items.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var hasLoaded = false

    // ── Load once

    fun load() {
        if (hasLoaded) return
        viewModelScope.launch { fetchBillboards() }
    }

    // ── Force reload

    fun refresh() {
        viewModelScope.launch { fetchBillboards() }
    }

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    // ── Private fetch

    private suspend fun fetchBillboards() {
        _isLoading.value = true
        _errorMessage.value = null
        try {
            _items.value = service.fetchBillboards()
            hasLoaded = true
        } catch (e: Exception) {
            _errorMessage.value = e.message
        } finally {
            _isLoading.value = false
        }
    }
}