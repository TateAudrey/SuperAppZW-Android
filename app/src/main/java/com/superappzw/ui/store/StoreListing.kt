package com.superappzw.ui.store

import com.google.firebase.Timestamp

data class StoreListing(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
    val itemCode: String,
    val imageURL: String?,
    val viewCount: Int,
    val ownerUserID: String,
    val location: String = "",
    val isNegotiable: Boolean = false,
    val createdAt: Timestamp? = null,
) {
    // Convenience — mirrors Swift's displayPrice computed property
    val displayPrice: String
        get() = if (isNegotiable) "Negotiable" else formatPrice()

    private fun formatPrice(): String {
        return if (price % 1.0 == 0.0) {
            "$currency ${price.toInt()}"
        } else {
            "$currency ${"%.2f".format(price)}"
        }
    }

    // Equality and hash based on itemCode — mirrors Swift's Hashable conformance
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StoreListing) return false
        return itemCode == other.itemCode
    }

    override fun hashCode(): Int {
        return itemCode.hashCode()
    }
}