package com.superappzw.ui.store

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.superappzw.ui.lisitngs.ListingCard
import com.superappzw.ui.lisitngs.ListingModel
import com.superappzw.ui.theme.SuperAppZWTheme


// Non-lazy 2-column grid — safe to use inside a parent verticalScroll().
// LazyVerticalGrid cannot be nested inside Column(Modifier.verticalScroll())
// because it requires bounded height constraints.

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreProductsGrid(
    listings: List<StoreListing>,
    onSelect: ((StoreListing) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val rows = listings.chunked(2)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowItems.forEach { listing ->
                    ListingCard(
                        model = ListingModel(
                            title = listing.title,
                            description = listing.description,
                            price = listing.price,
                            currency = listing.currency,
                            itemCode = listing.itemCode,
                            imageURL = listing.imageURL,
                            viewCount = listing.viewCount,
                            ownerUserID = listing.ownerUserID
                        ),
                        onTap = { onSelect?.invoke(listing) },
                        modifier = Modifier.weight(1f),
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
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