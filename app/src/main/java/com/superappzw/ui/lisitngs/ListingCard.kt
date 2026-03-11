package com.superappzw.ui.lisitngs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListingCard(
    model: ListingModel,
    isFavourited: Boolean = false,
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    ListingGridItemView(
        url = model.imageURL ?: "",
        title = model.title,
        description = model.description,
        price = model.price,
        currency = model.currency,
        itemCode = model.itemCode,
        viewCount = model.viewCount,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onTap?.invoke() },
                onLongClick = { onLongPress?.invoke() },
            ),
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ListingCardPreview() {
    SuperAppZWTheme {
        ListingCard(
            model = ListingModel(
                title = "iPhone 15 Pro Max",
                description = "Like new condition - 128GB Space Black",
                price = 950.0,
                currency = "USD",
                itemCode = "IPH15PM-XYZ123",
                imageURL = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300",
                viewCount = 247,
                ownerUserID = "user123",
            ),
            isFavourited = true,
            modifier = Modifier.padding(16.dp),
        )
    }
}