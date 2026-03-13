package com.superappzw.ui.store

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.superappzw.services.ListingService
import com.superappzw.ui.components.utils.abbreviated
import com.superappzw.ui.components.utils.formattedPrice
import com.superappzw.ui.theme.PrimaryColor

@Composable
fun StoreListingDetailView(
    itemCode: String,
    ownerUserID: String,
    modifier: Modifier = Modifier,
    viewModel: StoreListingDetailViewModel = viewModel(),
) {
    val listing      by viewModel.listing.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isFavourited by viewModel.isFavourited.collectAsState()

    LaunchedEffect(itemCode, ownerUserID) {
        viewModel.load(itemCode = itemCode, ownerUserID = ownerUserID)
    }

    when {
        isLoading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize().background(Color(0xFFF2F2F7)),
            ) { CircularProgressIndicator(color = PrimaryColor) }
        }
        errorMessage != null -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize().background(Color(0xFFF2F2F7)),
            ) {
                Text(text = errorMessage ?: "Something went wrong.", color = Color.Gray, fontSize = 15.sp)
            }
        }
        listing != null -> {
            StoreListingDetailContent(
                listing      = listing!!,
                isFavourited = isFavourited,
                onFavouriteToggle = { viewModel.toggleFavourite() },
                modifier     = modifier,
            )
        }
    }
}

// ── Detail content ────────────────────────────────────────────────────────────

@Composable
private fun StoreListingDetailContent(
    listing: StoreListing,
    isFavourited: Boolean,
    onFavouriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listingService = remember { ListingService() }

    val heartTint by animateColorAsState(
        targetValue = if (isFavourited) Color.Red else Color.White,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "heartTint",
    )

    LaunchedEffect(listing.itemCode) {
        listingService.recordView(
            itemCode    = listing.itemCode,
            ownerUserID = listing.ownerUserID,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        // ── Image + Favourite button ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            if (!listing.imageURL.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = listing.imageURL,
                    contentDescription = listing.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    loading = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE5E5EA)),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = PrimaryColor,
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE5E5EA)),
                        )
                    },
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE5E5EA)),
                )
            }

            IconButton(
                onClick = onFavouriteToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f)),
            ) {
                Icon(
                    imageVector = if (isFavourited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavourited) "Unfavourite" else "Favourite",
                    tint = heartTint,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // ── Title + Views + Price ─────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = listing.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.RemoveRedEye,
                        contentDescription = "Views",
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = listing.viewCount.abbreviated(),
                        fontSize = 13.sp,
                        color = Color.Gray,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = buildAnnotatedString {
                    append("Price: ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = PrimaryColor)) {
                        append(
                            if (listing.isNegotiable) "Negotiable"
                            else listing.price.formattedPrice(listing.currency)
                        )
                    }
                },
                fontSize = 14.sp,
                color = Color.Black,
            )
        }

        // ── Item code ─────────────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(text = "Code:", fontSize = 13.sp, color = Color.Gray)
            Text(
                text = listing.itemCode,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // ── Description ───────────────────────────────────────────────────────
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = "Description",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
            )
            Text(
                text = listing.description.ifBlank { "No description provided." },
                fontSize = 15.sp,
                color = Color.Gray,
                lineHeight = 22.sp,
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StoreListingDetailPreview() {
    StoreListingDetailContent(
        listing = StoreListing(
            title = "AirPods Pro 3",
            description = "Only a few months old. Mint condition and box is available.",
            price = 150.00,
            currency = "USD",
            itemCode = "ISA-ZW-1772102894-1527",
            imageURL = null,
            viewCount = 1527,
            ownerUserID = "UT0mHxc1IJcuRsibi3srlMbISZI2",
        ),
        isFavourited = false,
        onFavouriteToggle = {}
    )
}
