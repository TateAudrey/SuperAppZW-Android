package com.superappzw.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun CategoryPill(
    item: CategoryItem,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 14.dp, horizontal = 16.dp),
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.name,
            tint = PrimaryColor,
            modifier = Modifier.size(28.dp),
        )

        Spacer(modifier = Modifier.width(10.dp))

        // fixedSize() equivalent — Text is never truncated, drives its own width
        Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor,
            softWrap = false,
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Category Pill", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun CategoryPillPreview() {
    SuperAppZWTheme {
        CategoryPill(
            item = CategoryItem.all.first(),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Category Pill – long name", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun CategoryPillLongNamePreview() {
    SuperAppZWTheme {
        CategoryPill(
            item = CategoryItem.all[1],
            modifier = Modifier.padding(16.dp),
        )
    }
}