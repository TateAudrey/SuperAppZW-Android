package com.superappzw.ui.store


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
) {
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