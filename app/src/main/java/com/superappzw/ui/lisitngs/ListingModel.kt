package com.superappzw.ui.lisitngs

data class ListingModel(
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
    val itemCode: String,
    val imageURL: String?,
    val viewCount: Int,
)