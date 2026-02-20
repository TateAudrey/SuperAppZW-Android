package com.superappzw.ui.components.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.superappzw.R
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme
import kotlinx.coroutines.delay

@Composable
fun RotatingIcon(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.small_logo),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer {
                    rotationZ = rotation
                },
            colorFilter = ColorFilter.tint(PrimaryColor)
        )

        Text(
            text = "Loading, please wait...",
            style = MaterialTheme.typography.bodyLarge,  // ✅ Futura Book 16sp
            color = Color.Black
        )

    }
}


@Preview(showBackground = true)
@Composable
fun RotatingIconPreview() {
    SuperAppZWTheme {
        var trigger by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            while(true) {
                delay(100)
                trigger = !trigger
            }
        }
        RotatingIcon()
    }
}


