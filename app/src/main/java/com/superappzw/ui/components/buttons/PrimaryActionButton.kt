package com.superappzw.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryActionButton(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    enabled: Boolean = true

) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(
                color = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)  // ✅ GRAYED OUT
                },
                shape = RoundedCornerShape(32.dp)
            )
            .clickable(
                enabled = enabled,  // ✅ DISABLES CLICKS!
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) {
                Color.White
            } else {
                Color.White.copy(alpha = 0.6f)  // ✅ DIMMED TEXT
            }
        )
    }
}



@Preview(showBackground = true, name = "PrimaryActionButton Preview")
@Composable
fun PrimaryActionButtonPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            PrimaryActionButton(
                title = "Get Started",
                onClick = { /* Preview - no action */ }
            )
        }
    }
}

