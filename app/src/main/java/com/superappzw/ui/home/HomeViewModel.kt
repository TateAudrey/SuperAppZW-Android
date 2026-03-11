package com.superappzw.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    // ── Public entry points ───────────────────────────────────────────────────

    fun loadFeaturedListings(province: String?) {
        viewModelScope.launch { fetch(province) }
    }

    fun refreshListings(province: String? = null) {
        viewModelScope.launch { fetch(province) }
    }

    // ── Core fetch ────────────────────────────────────────────────────────────

    private suspend fun fetch(province: String? = null) {
        _isLoadingListings.value = true
        _errorMessage.value = null

        try {
            val snapshot = db.collection("listings").get().await()
            println("DEBUG: found ${snapshot.documents.size} listing documents")

            val allListings = mutableListOf<StoreListing>()

            for (document in snapshot.documents) {
                val data = document.data ?: continue
                @Suppress("UNCHECKED_CAST")
                val myListings = data["myListings"] as? List<Map<String, Any>> ?: continue
                println("DEBUG: user ${document.id} has ${myListings.size} listings")

                for (listing in myListings) {
                    val location = listing["location"] as? String ?: ""
                    println("DEBUG: listing '${listing["title"]}' location='$location'")
                    val mapped = listing.toStoreListing(ownerUserID = document.id) ?: continue
                    allListings.add(mapped)
                }
            }

            println("DEBUG: total mapped listings=${allListings.size}, province filter='$province'")

            val filtered = if (!province.isNullOrBlank()) {
                val normalized = province
                    .replace(" Province", "", ignoreCase = true)
                    .trim()
                    .lowercase()
                println("DEBUG: normalized province='$normalized'")
                allListings.filter {
                    val loc = it.location.replace(" Province", "", ignoreCase = true).trim().lowercase()
                    println("DEBUG: comparing '$loc' == '$normalized' → ${loc == normalized}")
                    loc == normalized
                }
            } else {
                allListings
            }

            println("DEBUG: filtered=${filtered.size}")
            _allListings.value = filtered

        } catch (e: Exception) {
            println("DEBUG ERROR: ${e.message}")
            _errorMessage.value = e.message
        } finally {
            _isLoadingListings.value = false
        }
    }

    // ── Inline mapping helper — mirrors ListingService.toStoreListing ─────────

    private fun Map<String, Any>.toStoreListing(ownerUserID: String): StoreListing? {
        val title          = this["title"]      as? String ?: return null
        val itemCode       = this["itemCode"]   as? String ?: return null
        val imageURLString = this["imageURL"]   as? String ?: return null
        val priceString    = this["price"]      as? String ?: return null
        val currency       = this["currency"]   as? String ?: return null
        val viewCount      = (this["viewCount"] as? Long)?.toInt() ?: 0
        val description    = this["description"] as? String ?: ""
        val location       = this["location"]   as? String ?: ""
        val isNegotiable   = priceString.trim() == "Negotiable"
        val price          = if (isNegotiable) 0.0 else priceString.toDoubleOrNull() ?: 0.0

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
        )
    }
}