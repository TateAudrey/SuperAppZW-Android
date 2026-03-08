package com.superappzw.ui.categories

import com.superappzw.ui.components.utils.EmptyStateView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

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

    // Load on first appearance — mirrors Swift .task
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(18.dp),
                        )
                        androidx.compose.foundation.layout.Spacer(
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Text(
                            text = category.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
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

                // ── Grid ──────────────────────────────────────────────────────
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(listings) { listing ->
                            // TODO: Replace with your real ListingCard composable
                            Text(
                                text = listing.title,
                                modifier = Modifier.padding(8.dp),
                            )
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