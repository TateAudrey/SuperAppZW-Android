package com.superappzw.ui.reviews

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.google.firebase.Timestamp
import com.superappzw.model.StoreReviewModel
import com.superappzw.ui.store.InitialsContent
import com.superappzw.ui.store.buildInitials
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailSheet(
    review: StoreReviewModel,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // ── Reviewer header ───────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Avatar
                val initials = buildInitials(review.reviewerName)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryColor),
                ) {
                    if (!review.reviewerImageURL.isNullOrBlank()) {
                        SubcomposeAsyncImage(
                            model = review.reviewerImageURL,
                            contentDescription = review.reviewerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            loading = { InitialsContent(initials = initials) },
                            error = { InitialsContent(initials = initials) },
                        )
                    } else {
                        InitialsContent(initials = initials)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = review.reviewerName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                    )

                    // Star row
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (star in 1..5) {
                            val filled = star <= review.rating
                            Icon(
                                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = if (filled) Color(0xFFFFCC00) else Color(0xFFAAAAAA),
                                modifier = Modifier.size(13.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            // ── Comment ───────────────────────────────────────────────────────
            Text(
                text = review.comment,
                fontSize = 15.sp,
                color = Color.Gray,
                lineHeight = 23.sp,
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ReviewDetailSheetPreview() {
    SuperAppZWTheme {
        ReviewDetailSheet(
            review = StoreReviewModel(
                id = "review_001",
                reviewerUID = "user123",
                reviewerName = "Jane Doe",
                reviewerImageURL = null,
                comment = "Absolutely fantastic seller! Item was exactly as described and shipped super fast. Would definitely buy again.",
                rating = 5,
                createdAt = Timestamp.now(),
            ),
            onDismiss = {},
        )
    }
}