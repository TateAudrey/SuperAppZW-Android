package com.superappzw.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
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

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    private var lastDocument: DocumentSnapshot? = null
    private var currentProvince: String? = null
    private val pageSize = 10L

    // ── Public entry points ───────────────────────────────────────────────────

    fun loadFeaturedListings(province: String?) {
        viewModelScope.launch {
            resetPagination(province)
            fetchNextPage()
        }
    }

    fun refreshListings(province: String? = null) {
        viewModelScope.launch {
            resetPagination(province)
            fetchNextPage()
        }
    }

    fun loadMoreIfNeeded() {
        if (!_hasMore.value || _isLoadingMore.value || _isLoadingListings.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            fetchNextPage()
            _isLoadingMore.value = false
        }
    }

    // ── Pagination helpers ────────────────────────────────────────────────────

    private fun resetPagination(province: String?) {
        _allListings.value = emptyList()
        lastDocument = null
        _hasMore.value = true
        currentProvince = province
        _errorMessage.value = null
    }

    private suspend fun fetchNextPage() {
        if (_allListings.value.isEmpty()) _isLoadingListings.value = true
        _errorMessage.value = null

        try {
            var query = db.collection("listings")
                .orderBy(FieldPath.documentId())
                .limit(pageSize)

            lastDocument?.let { query = query.startAfter(it) }

            val snapshot = query.get().await()
            val docs = snapshot.documents

            val newListings = mutableListOf<StoreListing>()

            for (document in docs) {
                val data = document.data ?: continue
                @Suppress("UNCHECKED_CAST")
                val myListings = data["myListings"] as? List<Map<String, Any>> ?: continue

                for (listing in myListings) {
                    val mapped = listing.toStoreListing(ownerUserID = document.id) ?: continue
                    newListings.add(mapped)
                }
            }

            // Apply province filter
            val filtered = if (!currentProvince.isNullOrBlank()) {
                val normalized = currentProvince!!
                    .replace(" Province", "", ignoreCase = true)
                    .trim()
                    .lowercase()
                newListings.filter {
                    val loc = it.location
                        .replace(" Province", "", ignoreCase = true)
                        .trim()
                        .lowercase()
                    loc == normalized
                            || loc == "all provinces"   // ← always include nationwide listings
                            || loc.isBlank()            // ← include listings with no location set
                }
            } else {
                newListings
            }

            // Sort by viewCount descending
            val sorted = filtered.sortedByDescending { it.createdAt?.seconds ?: 0L }

            // Append to existing list
            _allListings.value = _allListings.value + sorted

            // Update cursor and hasMore
            lastDocument = docs.lastOrNull()
            _hasMore.value = docs.size.toLong() == pageSize

        } catch (e: Exception) {
            _errorMessage.value = e.message
        } finally {
            _isLoadingListings.value = false
        }
    }

    // ── Mapping helper ────────────────────────────────────────────────────────

    private fun Map<String, Any>.toStoreListing(ownerUserID: String): StoreListing? {
        val title          = this["title"]       as? String ?: return null
        val itemCode       = this["itemCode"]    as? String ?: return null
        val imageURLString = this["imageURL"]    as? String ?: return null
        val priceString    = this["price"]       as? String ?: return null
        val currency       = this["currency"]    as? String ?: return null
        val viewCount      = (this["viewCount"]  as? Long)?.toInt() ?: 0
        val description    = this["description"] as? String ?: ""
        val location       = this["location"]    as? String ?: ""
        val isNegotiable   = priceString.trim() == "Negotiable"
        val price          = if (isNegotiable) 0.0 else priceString.toDoubleOrNull() ?: 0.0
        val createdAt = this["createdAt"] as? com.google.firebase.Timestamp

        return StoreListing(
            title        = title,
            description  = description,
            price        = price,
            currency     = currency,
            itemCode     = itemCode,
            imageURL     = imageURLString.takeIf { it.isNotBlank() },
            viewCount    = viewCount,
            ownerUserID  = ownerUserID,
            location     = location,
            isNegotiable = isNegotiable,
            createdAt = createdAt,
        )
    }
}