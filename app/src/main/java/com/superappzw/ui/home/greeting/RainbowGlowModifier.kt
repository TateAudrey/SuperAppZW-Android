package com.superappzw.ui.home.greeting

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

private val RainbowColors = listOf(
    Color.Red,
    Color(0xFFFF8C00), // Orange
    Color.Yellow,
    Color.Green,
    Color.Cyan,
    Color.Blue,
    Color(0xFF8B00FF), // Purple/Violet
    Color.Red,         // Loop back to red for seamless repeat
)

fun Modifier.rainbowGlow(isActive: Boolean): Modifier = composed {
    if (!isActive) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "rainbowGlow")

    // Animate phase from 0 → 1 continuously (maps to gradient sweep across text)
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rainbowPhase",
    )

    this
        // graphicsLayer needed so BlendMode.SrcIn composites correctly
        .graphicsLayer { alpha = 0.99f }
        .drawWithContent {
            // Draw the original text first
            drawContent()

            // Draw the animated rainbow gradient over it using SrcIn blend
            // so only the text pixels are colored (masks to text shape)
            val gradientWidth = size.width * 2f
            val offset = -gradientWidth / 2f + (gradientWidth * phase)

            drawRect(
                brush = Brush.linearGradient(
                    colors = RainbowColors,
                    start = Offset(offset, 0f),
                    end = Offset(offset + gradientWidth, 0f),
                ),
                blendMode = BlendMode.SrcIn,
            )
        }
}