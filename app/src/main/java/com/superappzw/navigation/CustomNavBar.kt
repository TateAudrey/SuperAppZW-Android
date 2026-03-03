package com.superappzw.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.SubcomposeAsyncImage
import com.superappzw.ui.home.NavTooltipData
import com.superappzw.ui.theme.GloriaHallelujah
import com.superappzw.ui.theme.GrayColor
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun CustomNavBar(
    title: String,
    profileImageURL: String?,
    userName: String = "",
    tooltipData: NavTooltipData? = null,
    onProfileTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    // ── Use remember (not rememberSaveable) to avoid stale state ─────────────
    var showTooltip by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {

            // ── Title ─────────────────────────────────────────────────────────
            val titleColor by animateColorAsState(
                targetValue = if (showTooltip) PrimaryColor else MaterialTheme.colorScheme.onBackground,
                animationSpec = tween(200),
                label = "titleColor",
            )

            Text(
                text = title,
                fontFamily = GloriaHallelujah,
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (tooltipData != null) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { showTooltip = !showTooltip }
                        } else Modifier
                    ),
            )

            Spacer(modifier = Modifier.size(12.dp))

            // ── Profile circle ────────────────────────────────────────────────
            ProfileCircle(
                profileImageURL = profileImageURL,
                userName = userName,
                onTap = onProfileTap,
            )
        }

        // ── Tooltip — rendered inside Box, directly below title ───────────────
        if (tooltipData != null && showTooltip) {
            Popup(
                onDismissRequest = { showTooltip = false },
                properties = PopupProperties(focusable = true),
            ) {
                AnimatedVisibility(
                    visible = showTooltip,
                    enter = fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.92f),
                    exit = fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.92f),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = tooltipData.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryColor,
                                letterSpacing = 0.5.sp,
                            )
                            Text(
                                text = tooltipData.subtitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = GrayColor,
                                lineHeight = 20.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── ProfileCircle ─────────────────────────────────────────────────────────────

@Composable
private fun ProfileCircle(
    profileImageURL: String?,
    userName: String,
    onTap: (() -> Unit)?,
) {
    val initial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: ""

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(42.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
            )
            .clip(CircleShape)
            .background(PrimaryColor)
            .then(
                if (onTap != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap,
                ) else Modifier
            ),
    ) {
        if (!profileImageURL.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = profileImageURL,
                contentDescription = "Profile picture of $userName",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                error = { PlaceholderContent(initial = initial) },
                loading = { PlaceholderContent(initial = initial) },
            )
        } else {
            PlaceholderContent(initial = initial)
        }
    }
}

// ── Placeholder ───────────────────────────────────────────────────────────────

@Composable
private fun PlaceholderContent(initial: String) {
    if (initial.isNotEmpty()) {
        Text(
            text = initial,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    } else {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp),
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Nav Bar – tooltip visible", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun CustomNavBarTooltipPreview() {
    SuperAppZWTheme {
        CustomNavBar(
            title = "Mangwanani, Tatenda",
            profileImageURL = null,
            userName = "Tatenda Moyo",
            tooltipData = NavTooltipData(
                title = "ChiShona",
                subtitle = "Mangwanani — Good morning in Shona",
            ),
            onProfileTap = {},
        )
    }
}

@Preview(name = "Nav Bar – no tooltip", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun CustomNavBarNoTooltipPreview() {
    SuperAppZWTheme {
        CustomNavBar(
            title = "Linjani, Audrey",
            profileImageURL = null,
            userName = "Audrey Dube",
            onProfileTap = {},
        )
    }
}
