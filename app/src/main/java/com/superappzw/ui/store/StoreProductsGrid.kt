package com.superappzw.ui.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.superappzw.ui.lisitngs.ListingCard
import com.superappzw.ui.lisitngs.ListingModel
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun StoreProductsGrid(
    listings: List<StoreListing>,
    onSelect: ((StoreListing) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        items(listings, key = { it.itemCode }) { listing ->
            ListingCard(
                model = ListingModel(
                    title = listing.title,
                    description = listing.description,
                    price = listing.price,
                    currency = listing.currency,
                    itemCode = listing.itemCode,
                    imageURL = listing.imageURL,
                    viewCount = listing.viewCount,
                ),
                onTap = { onSelect?.invoke(listing) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun StoreProductsGridPreview() {
    SuperAppZWTheme {
        StoreProductsGrid(
            listings = listOf(
                StoreListing(
                    title = "AirPods Pro 3",
                    description = "Mint condition, box included.",
                    price = 150.0,
                    currency = "USD",
                    itemCode = "ISA-ZW-001",
                    imageURL = null,
                    viewCount = 42,
                    ownerUserID = "user_001",
                ),
                StoreListing(
                    title = "iPhone 15 Pro Max",
                    description = "Like new - 128GB Space Black.",
                    price = 950.0,
                    currency = "USD",
                    itemCode = "ISA-ZW-002",
                    imageURL = null,
                    viewCount = 247,
                    ownerUserID = "user_001",
                ),
                StoreListing(
                    title = "Samsung 4K Monitor",
                    description = "27 inch, HDR, 144Hz.",
                    price = 320.0,
                    currency = "USD",
                    itemCode = "ISA-ZW-003",
                    imageURL = null,
                    viewCount = 89,
                    ownerUserID = "user_002",
                ),
            ),
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}