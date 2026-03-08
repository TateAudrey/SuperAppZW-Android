package com.superappzw.ui.home.province

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun ProvinceDropDown(
    viewModel: ProvinceViewModel,
    modifier: Modifier = Modifier,
) {
    val provinces by viewModel.provinces.collectAsState()
    val selectedProvince by viewModel.selectedProvince.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var isExpanded by remember { mutableStateOf(false) }

    // Fetch on first appearance — mirrors Swift .task { await store.load() }
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {

        // ── Trigger button ────────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp),
                    ambientColor = Color.Black.copy(alpha = 0.07f))
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { isExpanded = !isExpanded }
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(20.dp),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = selectedProvince ?: "Select Location",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (selectedProvince != null) Color(0xFF1A1A1A) else Color(0xFF9E9E9E),
                modifier = Modifier.weight(1f),
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = PrimaryColor,
                )
            } else {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // ── Expanded list ─────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                    expandVertically(spring(stiffness = Spring.StiffnessMedium)),
            exit = fadeOut(spring(stiffness = Spring.StiffnessMedium)) +
                    shrinkVertically(spring(stiffness = Spring.StiffnessMedium)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp),
                        ambientColor = Color.Black.copy(alpha = 0.07f))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
            ) {
                provinces.forEachIndexed { index, province ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                viewModel.selectProvince(province)
                                isExpanded = false
                            }
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                    ) {
                        Text(
                            text = province,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF1A1A1A),
                            modifier = Modifier.weight(1f),
                        )

                        if (selectedProvince == province) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = PrimaryColor,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }

                    // Divider between items, not after last
                    if (index < provinces.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFF0F0F0)
                        )
                    }
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ProvinceDropDownPreview() {
    SuperAppZWTheme {
        // Simulate a loaded ViewModel state for preview
        Box(modifier = Modifier.padding(top = 20.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Preview note: connect a real ProvinceViewModel in HomeView",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
        }
    }
}