package com.superappzw.model
import com.google.firebase.Timestamp

data class StoreReviewModel(
    val id: String = "",
    val reviewerUID: String,
    val reviewerName: String,
    val reviewerImageURL: String?,
    val comment: String,
    val rating: Int,
    val createdAt: Timestamp?,
)

data class StoreRatingAggregate(
    val averageRating: Double,
    val totalReviews: Int,
)