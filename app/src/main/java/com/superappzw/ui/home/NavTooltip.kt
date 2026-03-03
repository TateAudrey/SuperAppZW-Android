package com.superappzw.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.superappzw.ui.theme.GrayColor
import com.superappzw.ui.theme.PrimaryColor

// ── Data model ────────────────────────────────────────────────────────────────

data class NavTooltipData(
    val title: String,
    val subtitle: String
)

// ── Dummy data (replace with Firebase later) ──────────────────────────────────

object TooltipDefaults {
    // TODO: replace with real DailyLanguage from Firebase
    fun dummyTooltip() = NavTooltipData(
        title = "Language: Shona",
        subtitle = "Tendai — a Shona name meaning"
    )
}

// ── Tooltip popup ─────────────────────────────────────────────────────────────

@Composable
fun NavTooltipPopup(
    visible: Boolean,
    data: NavTooltipData,
    onDismiss: () -> Unit,
) {
    if (visible) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true),
        ) {
            AnimatedVisibility(
                visible = visible,
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = data.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor,
                            letterSpacing = 0.5.sp,
                        )
                        Text(
                            text = data.subtitle,
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