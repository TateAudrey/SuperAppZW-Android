package com.superappzw.ui.home.province

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.services.ProvinceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProvinceViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("province_prefs", Context.MODE_PRIVATE)
    private val selectedKey = "selectedProvince"
    private val service = ProvinceService()

    private val _provinces = MutableStateFlow<List<String>>(emptyList())
    val provinces: StateFlow<List<String>> = _provinces.asStateFlow()

    private val _selectedProvince = MutableStateFlow<String?>(null)
    val selectedProvince: StateFlow<String?> = _selectedProvince.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Restore last selected province on launch
        _selectedProvince.value = prefs.getString(selectedKey, null)
    }

    fun clearSelection() {
        _selectedProvince.value = null
        prefs.edit().remove(selectedKey).apply()
    }

    fun load() {
        if (_provinces.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetched = service.fetchProvinces()
                // Prepend "All Provinces" so the user can always reset the filter
                _provinces.value = listOf("All Provinces") + fetched
            } catch (e: Exception) {
                // expose error if needed
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectProvince(province: String) {
        if (province == "All Provinces") {
            clearSelection()
        } else {
            _selectedProvince.value = province
            prefs.edit().putString(selectedKey, province).apply()
        }
    }
}