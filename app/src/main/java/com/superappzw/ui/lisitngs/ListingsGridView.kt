package com.superappzw.ui.lisitngs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.ui.store.StoreListing
import com.superappzw.ui.theme.SuperAppZWTheme

// Non-lazy 2-column grid — safe to use inside a parent verticalScroll().
// LazyVerticalGrid cannot be nested inside Column(Modifier.verticalScroll())
// because it requires bounded height constraints.

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListingsGridView(
    listings: List<StoreListing>,
    onTap: (itemCode: String, ownerUserID: String) -> Unit,
    onLongPress: ((StoreListing) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

    // Chunk into rows of 2 — mirrors LazyVGrid(columns: [.flexible(), .flexible()])
    val rows = listings.chunked(2)

    androidx.compose.foundation.layout.Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowItems.forEach { listing ->
                    val isOwner = currentUserID != null && listing.ownerUserID == currentUserID

                    ListingCard(
                        model = ListingModel(
                            title = listing.title,
                            description = listing.description,
                            price = listing.price,
                            currency = listing.currency,
                            isNegotiable = listing.isNegotiable,
                            itemCode = listing.itemCode,
                            imageURL = listing.imageURL,
                            viewCount = listing.viewCount,
                            ownerUserID = listing.ownerUserID,
                        ),
                        onTap = { onTap(listing.itemCode, listing.ownerUserID) },
                        onLongPress = if (isOwner) ({ onLongPress?.invoke(listing) }) else null,
                        modifier = Modifier.weight(1f),
                    )
                }

                // If the last row has only 1 item, fill the second slot with empty space
                if (rowItems.size == 1) {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ListingsGridViewPreview() {
    SuperAppZWTheme {
        ListingsGridView(
            listings = listOf(
                StoreListing(
                    title = "AirPods Pro",
                    description = "Mint condition",
                    price = 150.0,
                    currency = "USD",
                    itemCode = "ISA-ZW-001",
                    imageURL = null,
                    viewCount = 12,
                    ownerUserID = "test-user",
                ),
                StoreListing(
                    title = "Rubiks Cube",
                    description = "Mind tool",
                    price = 2.0,
                    currency = "USD",
                    itemCode = "ISA-ZW-002",
                    imageURL = null,
                    viewCount = 3,
                    ownerUserID = "other-user",
                ),
                StoreListing(
                    title = "Samsung Monitor",
                    description = "27 inch 4K",
                    price = 320.0,
                    currency = "USD",
                    itemCode = "ISA-ZW-003",
                    imageURL = null,
                    viewCount = 89,
                    ownerUserID = "other-user",
                ),
            ),
            onTap = { _, _ -> },
            onLongPress = {},
        )
    }
}
