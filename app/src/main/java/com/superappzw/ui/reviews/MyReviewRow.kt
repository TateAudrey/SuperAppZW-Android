package com.superappzw.ui.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.superappzw.model.MyReviewModel
import com.superappzw.model.StoreReviewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun MyReviewRow(
    item: MyReviewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation     = 4.dp,
                shape         = RoundedCornerShape(14.dp),
                ambientColor  = Color.Black.copy(alpha = 0.06f),
                spotColor     = Color.Black.copy(alpha = 0.06f),
            )
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp),
    ) {
        // ── Store name + date ─────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector    = Icons.Filled.Storefront,
                contentDescription = null,
                tint           = PrimaryColor,
                modifier       = Modifier.size(13.dp),
            )
            Text(
                text       = item.storeName,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = PrimaryColor,
                maxLines   = 1,
                modifier   = Modifier.weight(1f),
            )
            item.review.createdAt?.toDate()?.let { date ->
                Text(
                    text     = toFormattedDateShort(date),
                    fontSize = 11.sp,
                    color    = Color.Gray,
                )
            }
        }

        // ── Star rating ───────────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            for (star in 1..5) {
                Icon(
                    imageVector = if (star <= item.review.rating) Icons.Filled.Star
                    else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (star <= item.review.rating) Color(0xFFFFCC00)
                    else Color(0xFFBDBDBD),
                    modifier = Modifier.size(13.dp),
                )
            }
        }

        // ── Comment — no line limit, grows with content ───────────────────────
        Text(
            text     = item.review.comment,
            fontSize = 14.sp,
            color    = Color.Gray,
        )
    }
}

// ── Helper ────────────────────────────────────────────────────────────────────

private fun toFormattedDateShort(date: java.util.Date): String =
    java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(date)

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun MyReviewRowPreview() {
    SuperAppZWTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            MyReviewRow(item = MyReviewModel(
                storeID   = "store1",
                storeName = "Tendai's Tech Store",
                review    = StoreReviewModel(
                    id           = "review1",
                    reviewerUID  = "user1",
                    reviewerName = "Audrey Chanakira",
                    comment      = "Great seller, fast delivery and excellent communication. Would definitely buy again!",
                    rating       = 5,
                    createdAt    = Timestamp.now(),
                ),
            ))
            MyReviewRow(item = MyReviewModel(
                storeID   = "store2",
                storeName = "Chipo's Fashion Boutique",
                review    = StoreReviewModel(
                    id           = "review2",
                    reviewerUID  = "user1",
                    reviewerName = "Audrey Chanakira",
                    comment      = "Good quality items but delivery took a bit longer than expected.",
                    rating       = 3,
                    createdAt    = Timestamp.now(),
                ),
            ))
            MyReviewRow(item = MyReviewModel(
                storeID   = "store3",
                storeName = "Mukoma's Electronics",
                review    = StoreReviewModel(
                    id           = "review3",
                    reviewerUID  = "user1",
                    reviewerName = "Audrey Chanakira",
                    comment      = "Not satisfied with the product condition.",
                    rating       = 1,
                    createdAt    = Timestamp.now(),
                ),
            ))
        }
    }
}