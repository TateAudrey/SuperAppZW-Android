package com.superappzw.ui.lisitngs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.ui.store.StoreListing
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListingsGridView(
    listings: List<StoreListing>,
    onTap: (StoreListing) -> Unit,
    onLongPress: ((StoreListing) -> Unit)? = null,
    modifier: Modifier = Modifier,
    // Hoisted currentUserID to avoid Firebase initialization errors in Previews.
    // Default value uses LocalInspectionMode to safely bypass Firebase when rendering in the IDE.
    currentUserID: String? = if (LocalInspectionMode.current) null else FirebaseAuth.getInstance().currentUser?.uid
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        items(listings, key = { it.itemCode }) { listing ->
            val isOwner = currentUserID != null && listing.ownerUserID == currentUserID

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
                onTap = { onTap(listing) },
                onLongPress = if (isOwner) ({ onLongPress?.invoke(listing) }) else null,
                modifier = Modifier.fillMaxWidth(),
            )
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
            ),
            onTap = {},
            onLongPress = {},
            currentUserID = "test-user"
        )
    }
}
