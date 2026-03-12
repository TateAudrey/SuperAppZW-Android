package com.superappzw.ui.accountStatus

import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.R
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun GateView(
    title: String,
    message: String,
    icon: ImageVector,
    iconTint: Color,
    buttonLabel: String,
    rotatesIcon: Boolean = false,
    onButton: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor),
    ) {

        // ── Main content column ───────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                // Reserve space at the bottom so content never hides behind doodle
                .padding(horizontal = 32.dp)
                .padding(bottom = 140.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            StackedCardIcon(
                icon = icon,
                iconTint = iconTint,
                rotatesIcon = rotatesIcon,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Start,
                lineHeight = 26.sp,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(2f))
        }

        // ── Button + doodle pinned to the bottom ──────────────────────────────
        // Both live in a Box so the button can float on top of the doodle
        // via draw order (last child = on top in Compose Box)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        ) {
            // Doodle — drawn first, sits behind
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.bottom_doodle),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .align(Alignment.BottomCenter),
            )

            // Button — drawn second, floats on top of the doodle
            Button(
                onClick = onButton,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryColor,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(horizontal = 32.dp)
                    .align(Alignment.TopCenter),
            ) {
                Text(
                    text = buttonLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

// ── Stacked card icon ─────────────────────────────────────────────────────────

@Composable
private fun StackedCardIcon(
    icon: ImageVector,
    iconTint: Color,
    rotatesIcon: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gate_icon")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "icon_rotation",
    )

    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5_000
                0f   at 0
                -18f at 400  using LinearEasing
                0f   at 800  using LinearEasing
                0f   at 5_000
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "icon_bounce",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(160.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_main_logo),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(140.dp),
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(44.dp)
                .offset(x = 6.dp, y = if (rotatesIcon) 0.dp else bounceOffset.dp)
                .rotate(if (rotatesIcon) rotation else 0f),
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GateViewForceUpdatePreview() {
    SuperAppZWTheme {
        GateView(
            title = "Update App",
            message = "A new version of SuperApp ZW is available. Please update to continue using the app.",
            icon = androidx.compose.material.icons.Icons.Filled.Settings,
            iconTint = PrimaryColor,
            buttonLabel = "Update",
            rotatesIcon = true,
            onButton = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GateViewMaintenancePreview() {
    SuperAppZWTheme {
        GateView(
            title = "Under Maintenance",
            message = "We're making SuperApp ZW better. Please check back shortly.",
            icon = androidx.compose.material.icons.Icons.Filled.Build,
            iconTint = Color(0xFFFFC107),
            buttonLabel = "Retry",
            rotatesIcon = false,
            onButton = {},
        )
    }
}