package com.superappzw.ui.account.packages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun PackageHeaderCard(
    packageName: String,
    validDays: String,
    modifier: Modifier = Modifier,
) {
    val appearance = PackageAppearanceResolver.resolve(packageName)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        // ── Icon block ────────────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
                .background(appearance.color),
        ) {
            Image(
                painter = painterResource(id = appearance.iconRes),
                contentDescription = packageName,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.height(36.dp),
            )
        }

        // ── Text ──────────────────────────────────────────────────────────────
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .background(Color.White)
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = "Package: $packageName",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
            )
            Text(
                text = validDays,
                fontSize = 14.sp,
                color = Color.Gray,
            )
        }

        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.White),
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun PackageHeaderCardPreview() {
    SuperAppZWTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "ACTIVE PACKAGE",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp),
            )
            listOf(
                "Standard" to "Expires in 7 days",
                "Alpha"    to "Expires in 30 days",
                "Bravo"    to "Expires in 60 days",
                "Charlie"  to "Expires in 90 days",
                "Delta"    to "Expires in 365 days",
            ).forEach { (name, days) ->
                PackageHeaderCard(packageName = name, validDays = days)
            }
        }
    }
}