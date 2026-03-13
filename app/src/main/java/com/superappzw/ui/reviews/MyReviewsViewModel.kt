package com.superappzw.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.model.MyReviewModel
import com.superappzw.services.ReviewService
import com.superappzw.services.UserService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyReviewsViewModel : ViewModel() {

    private val reviewService = ReviewService()
    private val userService   = UserService.getInstance()

    private val _items        = MutableStateFlow<List<MyReviewModel>>(emptyList())
    val items: StateFlow<List<MyReviewModel>> = _items.asStateFlow()

    private val _isLoading    = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ── Load ──────────────────────────────────────────────────────────────────
    // Called on every appearance — mirrors Swift's onAppear { Task { await viewModel.load() } }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetched = reviewService.fetchMyReviews().toMutableList()

                // Enrich with store names in parallel
                fetched.indices.map { i ->
                    async {
                        val profile = runCatching {
                            userService.fetchProfile(fetched[i].storeID)
                        }.getOrNull()
                        val name = when {
                            profile?.virtualShopName?.isNotBlank() == true -> profile.virtualShopName
                            profile?.fullName?.isNotBlank() == true         -> profile.fullName
                            else                                             -> "Unknown Store"
                        }
                        i to name
                    }
                }.awaitAll().forEach { (i, name) ->
                    fetched[i] = fetched[i].copy(storeName = name)
                }

                _items.value = fetched
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    fun delete(item: MyReviewModel) {
        val reviewID = item.review.id.ifBlank { return }
        // Optimistic removal
        _items.value = _items.value.filter { it.id != item.id }
        viewModelScope.launch {
            try {
                reviewService.deleteMyReview(storeID = item.storeID, reviewID = reviewID)
            } catch (e: Exception) {
                _errorMessage.value = e.message
                load() // restore on failure
            }
        }
    }
}