package com.superappzw.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun StoreServicesTab(
    services: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f),
            )
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White),
    ) {
        services.forEachIndexed { index, service ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = service,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                )
            }

            if (index < services.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun StoreServicesTabPreview() {
    SuperAppZWTheme {
        StoreServicesTab(
            services = listOf(
                "Home Delivery within Harare",
                "Household Cleaning",
                "Custom Catering Packs for Events",
            ),
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}