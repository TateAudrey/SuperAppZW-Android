package com.superappzw.ui.lisitngs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme
import com.google.firebase.Timestamp
import com.superappzw.model.StoreReviewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReviewTabView(
    viewModel: MyListingsViewModel,
    modifier: Modifier = Modifier,
) {
    val receivedReviews by viewModel.receivedReviews.collectAsState()
    val isLoadingReviews by viewModel.isLoadingReviews.collectAsState()

    when {
        isLoadingReviews -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        }

        receivedReviews.isEmpty() -> {
            ReviewEmptyState(modifier = modifier)
        }

        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 30.dp),
            ) {
                items(receivedReviews, key = { it.id }) { review ->
                    ReviewRow(review = review)
                }
            }
        }
    }
}

// ── Review row ────────────────────────────────────────────────────────────────

@Composable
private fun ReviewRow(
    review: StoreReviewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // ── Avatar ────────────────────────────────────────────────────────
            SubcomposeAsyncImage(
                model = review.reviewerImageURL.takeIf { it?.isNotBlank() ?: false },
                contentDescription = review.reviewerName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                loading = { PersonPlaceholder() },
                error = { PersonPlaceholder() },
            )

            Spacer(modifier = Modifier.width(10.dp))

            // ── Name + stars ──────────────────────────────────────────────────
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = review.reviewerName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    for (i in 0 until 5) {
                        Icon(
                            imageVector = if (i < review.rating) Icons.Filled.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (i < review.rating) Color(0xFFFFCC00) else Color.Gray,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${review.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                    )
                }
            }

            // ── Date ──────────────────────────────────────────────────────────
            Text(
                text = review.createdAt?.toFormattedDate() ?: "",
                fontSize = 11.sp,
                color = Color.Gray,
            )
        }

        // ── Comment ───────────────────────────────────────────────────────────
        Text(
            text = review.comment,
            fontSize = 15.sp,
            color = Color.Black,
            lineHeight = 21.sp,
        )
    }
}

// ── Person placeholder ────────────────────────────────────────────────────────

@Composable
private fun PersonPlaceholder() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFFE5E5EA)),
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp),
        )
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun ReviewEmptyState(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 80.dp, start = 40.dp, end = 40.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Star,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "You haven't received any reviews yet",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

// ── Timestamp extension ───────────────────────────────────────────────────────

private fun Timestamp.toFormattedDate(): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(toDate())
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Reviews Loaded", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ReviewTabViewLoadedPreview() {
    SuperAppZWTheme {
        val mockReviews = listOf(
            StoreReviewModel(
                id = "review_001",
                reviewerUID = "user123",
                reviewerName = "Sarah M.",
                reviewerImageURL = "",
                comment = "Excellent service! Very professional and delivered on time. Will definitely use again.",
                rating = 5,
                createdAt = Timestamp.now(),
            ),
            StoreReviewModel(
                id = "review_002",
                reviewerUID = "user456",
                reviewerName = "Michael T.",
                reviewerImageURL = "",
                comment = "Good quality products at fair prices. Fast delivery too!",
                rating = 4,
                createdAt = Timestamp.now(),
            ),
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            items(mockReviews, key = { it.id }) { review ->
                ReviewRow(review = review)
            }
        }
    }
}

@Preview(name = "No Reviews", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ReviewTabViewEmptyPreview() {
    SuperAppZWTheme {
        ReviewEmptyState()
    }
}
