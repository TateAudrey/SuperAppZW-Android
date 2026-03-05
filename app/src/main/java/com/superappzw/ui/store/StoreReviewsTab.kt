package com.superappzw.ui.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.superappzw.model.StoreReviewModel
import com.superappzw.ui.components.utils.EmptyStateView
import com.superappzw.ui.reviews.ReviewViewModel
import com.superappzw.ui.reviews.StoreReviewRow
import com.superappzw.ui.theme.PrimaryColor

@Composable
fun StoreReviewsTab(
    viewModel: ReviewViewModel,
    modifier: Modifier = Modifier,
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()
    val totalReviews by viewModel.totalReviews.collectAsState()

    StoreReviewsTab(
        isLoading = isLoading,
        reviews = reviews,
        averageRating = averageRating,
        totalReviews = totalReviews,
        modifier = modifier
    )
}

@Composable
fun StoreReviewsTab(
    isLoading: Boolean,
    reviews: List<StoreReviewModel>,
    averageRating: Double,
    totalReviews: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        if (isLoading) {
            // ── Loading ───────────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = PrimaryColor,
                )
            }
        } else {
            // ── Rating summary ────────────────────────────────────────────────
            StoreRatingSummary(
                rating = averageRating,
                totalReviews = totalReviews,
            )

            // ── Reviews list or empty state ───────────────────────────────────
            if (reviews.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Outlined.Star,
                    message = "No reviews yet. Be the first!",
                    modifier = Modifier.padding(top = 30.dp),
                )
            } else {
                reviews.forEach { review ->
                    StoreReviewRow(review = review)
                }
            }
        }
    }
}
