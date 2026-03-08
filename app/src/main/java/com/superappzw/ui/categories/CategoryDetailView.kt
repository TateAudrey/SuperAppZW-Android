package com.superappzw.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.ui.components.utils.EmptyStateView
import com.superappzw.ui.lisitngs.ListingCard
import com.superappzw.ui.lisitngs.ListingModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailView(
    category: CategoryItem,
    onListingTap: (itemCode: String, ownerUserID: String) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val viewModel: CategoryDetailViewModel = viewModel(
        factory = CategoryDetailViewModelFactory(category),
    )

    val listings by viewModel.listings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryColor,
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = category.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.load() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {

                // ── Loading ───────────────────────────────────────────────────
                isLoading && listings.isEmpty() -> {
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                // ── Empty ─────────────────────────────────────────────────────
                listings.isEmpty() -> {
                    EmptyStateView(
                        icon = category.icon,
                        message = "No listings found in ${category.name}",
                    )
                }

                // ── Listings grid ─────────────────────────────────────────────
                // Uses LazyColumn with 2-per-row chunked layout —
                // avoids nested scroll issues with LazyVerticalGrid
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        val rows = listings.chunked(2)
                        items(rows) { rowItems ->
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
                                            ownerUserID = listing.ownerUserID,
                                        ),
                                        onTap = {
                                            onListingTap(listing.itemCode, listing.ownerUserID)
                                        },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                // Fill empty slot in last row if odd number of listings
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CategoryDetailViewPreview() {
    SuperAppZWTheme {
        CategoryDetailView(
            category = CategoryItem.all[1],
        )
    }
}