package com.superappzw.ui.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.theme.PrimaryColor

@Composable
fun StoreRatingSummary(
    rating: Double,
    totalReviews: Int,
    maxRating: Int = 5,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
    ) {
        // ── Numeric rating ────────────────────────────────────────────────────
        Text(
            text = "%.1f".format(rating),
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor,
        )

        // ── Star row ──────────────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (star in 1..maxRating) {
                val filled = star.toDouble() <= rating
                Icon(
                    imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (filled) Color(0xFFFFCC00) else Color(0xFFAAAAAA),
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}