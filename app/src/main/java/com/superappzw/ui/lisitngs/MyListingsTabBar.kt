package com.superappzw.ui.lisitngs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.remember
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun MyListingsTabBar(
    selectedTab: ListingTab,
    onTabSelected: (ListingTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        ListingTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab

            val textColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryColor else Color.Gray,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = Spring.StiffnessMedium,
                ),
                label = "tabColor_${tab.name}",
            )

            val indicatorColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryColor else Color.Transparent,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = Spring.StiffnessMedium,
                ),
                label = "indicatorColor_${tab.name}",
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabSelected(tab) },
                    )
                    .padding(vertical = 8.dp),
            ) {
                Text(
                    text = tab.label,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = textColor,
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Underline indicator — mirrors Rectangle().fill(...).frame(height: 2)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = indicatorColor,
                                cornerRadius = CornerRadius(1.dp.toPx()),
                            )
                        },
                )
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MyListingsTabBarPreview() {
    SuperAppZWTheme {
        MyListingsTabBar(
            selectedTab = ListingTab.PRODUCTS,
            onTabSelected = {},
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}