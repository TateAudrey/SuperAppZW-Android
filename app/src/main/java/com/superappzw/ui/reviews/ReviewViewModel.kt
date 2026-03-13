package com.superappzw.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superappzw.model.StoreReviewModel
import com.superappzw.services.ReviewService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {

    private val service = ReviewService()
    private var storeID = ""

    private val _reviews       = MutableStateFlow<List<StoreReviewModel>>(emptyList())
    val reviews: StateFlow<List<StoreReviewModel>> = _reviews.asStateFlow()

    private val _averageRating = MutableStateFlow(0.0)
    val averageRating: StateFlow<Double> = _averageRating.asStateFlow()

    private val _totalReviews  = MutableStateFlow(0)
    val totalReviews: StateFlow<Int> = _totalReviews.asStateFlow()

    private val _isLoading     = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage  = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _hasReviewed   = MutableStateFlow(false)
    val hasReviewed: StateFlow<Boolean> = _hasReviewed.asStateFlow()

    private val _isSubmitting  = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submitSuccess = MutableStateFlow(false)
    val submitSuccess: StateFlow<Boolean> = _submitSuccess.asStateFlow()

    // ── Load ──────────────────────────────────────────────────────────────────

    fun load(storeID: String) {
        this.storeID = storeID
        viewModelScope.launch {
            _isLoading.value    = true
            _errorMessage.value = null
            try {
                // Run all three fetches in parallel
                val reviewsDeferred     = async { service.fetchReviews(storeID) }
                val aggregateDeferred   = async { service.fetchRatingAggregate(storeID) }
                val hasReviewedDeferred = async { service.hasCurrentUserReviewed(storeID) }

                _reviews.value       = reviewsDeferred.await()
                val aggregate        = aggregateDeferred.await()
                _averageRating.value = aggregate?.averageRating ?: 0.0
                _totalReviews.value  = aggregate?.totalReviews ?: 0
                _hasReviewed.value   = hasReviewedDeferred.await()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    fun submitReview(storeOwnerUID: String, comment: String, rating: Int) {
        if (comment.isBlank() || rating <= 0) return
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null
            try {
                service.submitReview(
                    storeID       = storeID,
                    storeOwnerUID = storeOwnerUID,
                    comment       = comment,
                    rating        = rating,
                )
                _submitSuccess.value = true
                load(storeID)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    fun deleteReview(review: StoreReviewModel) {
        if (!review.isOwnReview) return
        // Optimistic local removal
        _reviews.value = _reviews.value.filter { it.id != review.id }
        viewModelScope.launch {
            try {
                service.deleteReview(storeID = storeID, reviewID = review.id)
                val aggregate        = service.fetchRatingAggregate(storeID)
                _averageRating.value = aggregate?.averageRating ?: 0.0
                _totalReviews.value  = aggregate?.totalReviews ?: 0
                _hasReviewed.value   = false
            } catch (e: Exception) {
                _errorMessage.value = e.message
                load(storeID) // restore on failure
            }
        }
    }
}