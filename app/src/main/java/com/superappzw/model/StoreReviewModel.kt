package com.superappzw.model
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

data class StoreReviewModel(
    val id: String = "",
    val reviewerUID: String = "",
    val reviewerName: String = "",
    val reviewerImageURL: String? = null,
    val comment: String = "",
    val rating: Int = 0,
    val createdAt: Timestamp? = null,
) {
    // True if the current user is the author of this review
    val isOwnReview: Boolean
        get() = FirebaseAuth.getInstance().currentUser?.uid == reviewerUID
}

data class StoreRatingAggregate(
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
)

data class MyReviewModel(
    val storeID: String,
    var storeName: String = "",
    val review: StoreReviewModel,
) {
    // storeID + reviewID combined — always unique, prevents SwiftUI/Compose identity collisions
    val id: String get() = "${storeID}_${review.id.ifBlank { storeID }}"
}