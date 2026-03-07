package com.superappzw.ui.lisitngs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = PrimaryColor,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp,
        ),
        modifier = modifier.size(56.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = "Edit",
            modifier = Modifier.size(22.dp),
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun FloatingActionButtonPreview() {
    SuperAppZWTheme {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize(),
        ) {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .padding(end = 20.dp, bottom = 150.dp),
            )
        }
    }
}