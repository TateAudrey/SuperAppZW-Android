package com.superappzw.ui.components.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun EmptyStateView(
    icon: ImageVector,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 80.dp)
            .padding(horizontal = 40.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFBDBDBD), // systemGray3 equivalent
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = message,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun EmptyStateViewPreview() {
    SuperAppZWTheme {
        EmptyStateView(
            icon = Icons.Filled.Inbox,
            message = "No products listed yet",
        )
    }
}