package com.superappzw.ui.store

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

// ── StoreTab ──────────────────────────────────────────────────────────────────

enum class StoreTab(val label: String) {
    PRODUCTS("Products"),
    SERVICES("Services"),
    REVIEWS("Reviews"),
}

// ── StoreTabBar ───────────────────────────────────────────────────────────────

@Composable
fun StoreTabBar(
    selectedTab: StoreTab,
    onTabSelected: (StoreTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        StoreTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab

            val textColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryColor else Color.Gray,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = Spring.StiffnessMedium,
                ),
                label = "tabTextColor_${tab.name}",
            )

            val indicatorColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryColor else Color.Transparent,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = Spring.StiffnessMedium,
                ),
                label = "tabIndicatorColor_${tab.name}",
            )

            TextButton(
                onClick = { onTabSelected(tab) },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = tab.label,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor,
                    )

                    // ── Underline indicator ───────────────────────────────────
                    androidx.compose.foundation.layout.Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .drawBehind {
                                drawRoundRect(
                                    color = indicatorColor,
                                    size = Size(size.width, size.height),
                                    cornerRadius = CornerRadius(1.dp.toPx()),
                                    topLeft = Offset.Zero,
                                )
                            },
                    )
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun StoreTabBarPreview() {
    SuperAppZWTheme {
        var selected by remember { mutableStateOf(StoreTab.PRODUCTS) }
        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            StoreTabBar(selectedTab = StoreTab.PRODUCTS, onTabSelected = {})
            StoreTabBar(selectedTab = StoreTab.SERVICES, onTabSelected = {})
            StoreTabBar(selectedTab = StoreTab.REVIEWS, onTabSelected = {})
        }
    }
}