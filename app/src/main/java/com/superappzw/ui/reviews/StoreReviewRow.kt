package com.superappzw.ui.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.Timestamp
import com.superappzw.model.StoreReviewModel
import com.superappzw.ui.store.InitialsContent
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun StoreReviewRow(
    review: StoreReviewModel,
    modifier: Modifier = Modifier,
) {
    var showDetail by remember { mutableStateOf(false) }
    val initials = buildInitials(review.reviewerName)

    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f),
            )
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable { showDetail = true }
            .padding(14.dp),
    ) {
        // ── Avatar ────────────────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor),
        ) {
            if (!review.reviewerImageURL.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = review.reviewerImageURL,
                    contentDescription = review.reviewerName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    loading = { InitialsContent(initials = initials) },
                    error = { InitialsContent(initials = initials) },
                )
            } else {
                InitialsContent(initials = initials)
            }
        }

        // ── Text content ──────────────────────────────────────────────────────
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = review.reviewerName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
            )
            Text(
                text = review.comment,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Posted: ${review.createdAt?.toFormattedDate() ?: "—"}",
                fontSize = 10.sp,
                color = Color(0xFF8E8E93),
            )
        }

        Spacer(modifier = Modifier.size(4.dp))

        // ── Chevron ───────────────────────────────────────────────────────────
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFAAAAAA),
            modifier = Modifier.size(18.dp),
        )
    }

    // ── Detail sheet ──────────────────────────────────────────────────────────
    if (showDetail) {
        ReviewDetailSheet(
            review = review,
            onDismiss = { showDetail = false },
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun buildInitials(name: String): String {
    val parts = name.trim().split(" ")
    val first = parts.firstOrNull()?.take(1)?.uppercase() ?: ""
    val last = parts.drop(1).firstOrNull()?.take(1)?.uppercase() ?: ""
    return "$first$last".ifBlank { "?" }
}

private fun Timestamp.toFormattedDate(): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(this.toDate())

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun StoreReviewRowPreview() {
    SuperAppZWTheme {
        StoreReviewRow(
            review = StoreReviewModel(
                id = "review_003",
                reviewerUID = "user789",
                reviewerName = "Anonymous User",
                reviewerImageURL = null,
                comment = "Quick response and great communication.",
                rating = 4,
                createdAt = Timestamp.now(),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}