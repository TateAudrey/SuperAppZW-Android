package com.superappzw.ui.home.billboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun BillboardSectionView(
    viewModel: BillboardViewModel,
    onStoreTap: (userID: String) -> Unit,
    modifier: Modifier = Modifier,
){
    val items by viewModel.items.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when {

            // ── Loading ───────────────────────────────────────────────────────
            isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                ) {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }

            // ── Error ─────────────────────────────────────────────────────────
            errorMessage != null -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                ) {
                    Text(
                        text = errorMessage!!,
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }
            }

            // ── Content ───────────────────────────────────────────────────────
            items.isNotEmpty() -> {
                val pagerState = rememberPagerState(
                    initialPage = currentIndex,
                    pageCount = { items.size },
                )

                // Keep ViewModel index in sync with pager swipes
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        viewModel.setCurrentIndex(page)
                    }
                }

                // ── Pager ─────────────────────────────────────────────────────
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                ) { page ->
                    BillboardCard(
                        item = items[page],
                        onTap = { onStoreTap(items[page].userID) },
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }

                // ── Page indicator dots ───────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items.indices.forEach { index ->
                        val isSelected = currentIndex == index

                        val dotSize by animateDpAsState(
                            targetValue = if (isSelected) 8.dp else 6.dp,
                            animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                            label = "dotSize_$index",
                        )
                        val dotColor by animateColorAsState(
                            targetValue = if (isSelected) PrimaryColor else Color(0xFFBDBDBD),
                            animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                            label = "dotColor_$index",
                        )

                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .clip(CircleShape)
                                .background(dotColor),
                        )

                        if (index < items.lastIndex) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Billboard – loading", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun BillboardSectionLoadingPreview() {
    SuperAppZWTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) {
            CircularProgressIndicator(
                color = PrimaryColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Preview(name = "Billboard – content", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun BillboardSectionContentPreview() {
    SuperAppZWTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            BillboardCard(
                item = BillboardItemModel(
                    id = "1",
                    userID = "user1",
                    title = "Cars for Sale",
                    detail = "We sell high quality cars across the country",
                    imageURL = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800",
                ),
                onTap = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            // Dots preview (static)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(true, false, false).forEachIndexed { index, isSelected ->
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryColor else Color(0xFFBDBDBD)),
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}
