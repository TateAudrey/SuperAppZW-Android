package com.superappzw.ui.lisitngs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.superappzw.ui.components.utils.abbreviated
import com.superappzw.ui.components.utils.formattedPrice
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun ListingGridItemView(
    url: String,
    title: String,
    description: String,
    price: Double,
    currency: String,
    itemCode: String,
    viewCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.07f),
                spotColor = Color.Black.copy(alpha = 0.07f),
            )
            .clip(RoundedCornerShape(14.dp))
    ) {

        // ── Image ─────────────────────────────────────────────────────────────
        SubcomposeAsyncImage(
            model = url.takeIf { it.isNotBlank() },
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            loading = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                ) {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                ) { }
            },
        )

        // ── Details ───────────────────────────────────────────────────────────
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(10.dp),
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Price: ",
                    fontSize = 12.sp,
                    color = Color(0xFF1A1A1A),
                )
                Text(
                    text = price.formattedPrice(currency),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Filled.RemoveRedEye,
                    contentDescription = "Views",
                    tint = Color.Gray,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = " ${viewCount.abbreviated()}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                )
            }

            HorizontalDivider(Modifier, thickness = 0.5.dp, color = Color(0xFFF0F0F0))

            Row {
                Text(
                    text = "Code: ",
                    fontSize = 11.sp,
                    color = Color(0xFF1A1A1A),
                )
                Text(
                    text = itemCode,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ListingGridItemViewPreview() {
    SuperAppZWTheme {
        ListingGridItemView(
            url = "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=400",
            title = "Eggs",
            description = "Dozen eggs",
            price = 4.00,
            currency = "USD",
            itemCode = "ISA-ZW-00123",
            viewCount = 1200,
            modifier = Modifier.padding(16.dp),
        )
    }
}
