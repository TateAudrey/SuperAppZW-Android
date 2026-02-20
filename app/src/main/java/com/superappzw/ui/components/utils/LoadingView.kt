package com.superappzw.ui.components.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun LoadingView(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Semi-transparent black overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        // Centered RotatingIcon
        RotatingIcon(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
fun LoadingViewPreview() {
    SuperAppZWTheme {
        LoadingView()
    }
}
