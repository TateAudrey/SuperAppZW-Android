package com.superappzw.ui.lisitngs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.home.HomeViewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun ListingsSectionView(
    viewModel: HomeViewModel,
    onTap: (itemCode: String, ownerUserID: String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listings by viewModel.allListings.collectAsState()
    val isLoading by viewModel.isLoadingListings.collectAsState()

    when {

        // ── Loading ───────────────────────────────────────────────────────────
        isLoading -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                CircularProgressIndicator(
                    color = PrimaryColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = "Loading listings...",
                    fontSize = 15.sp,
                    color = Color.Gray,
                )
            }
        }

        // ── Empty ─────────────────────────────────────────────────────────────
        listings.isEmpty() -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Inbox,
                    contentDescription = null,
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(40.dp),
                )
                Text(
                    text = "No listings available",
                    fontSize = 15.sp,
                    color = Color.Gray,
                )
                TextButton(onClick = onRefresh) {
                    Text(
                        text = "Refresh",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryColor,
                    )
                }
            }
        }

        // ── Grid (Manual Layout to avoid infinite height crash in Scrollable) ──
        else -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                listings.chunked(2).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        rowItems.forEach { listing ->
                            Box(modifier = Modifier.weight(1f)) {
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
                                        ownerUserID = listing.ownerUserID
                                    ),
                                    onTap = { onTap(listing.itemCode, listing.ownerUserID) },
                                )
                            }
                        }
                        // Add spacer if the row is not full to keep alignment
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ListingsSectionLoadingPreview() {
    SuperAppZWTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            CircularProgressIndicator(
                color = PrimaryColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(28.dp),
            )
            Text(text = "Loading listings...", fontSize = 15.sp, color = Color.Gray)
        }
    }
}
