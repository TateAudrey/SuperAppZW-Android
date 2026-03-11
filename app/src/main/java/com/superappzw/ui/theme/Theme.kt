package com.superappzw.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    background = IOSSystemBackground,
    surface = IOSSystemBackground,
)


@Composable
fun SuperAppZWTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // Define once

    MaterialTheme(
        colorScheme = colorScheme,  // Use the variable
        typography = Typography,    // ✅ Your Futura fonts are here!
        content = content
    )
}


