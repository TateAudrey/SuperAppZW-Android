package com.superappzw.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun AvatarView(
    imageURL: String?,
    initials: String,
    isUploading: Boolean = false,
    size: Dp = 88.dp,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clickable(enabled = !isUploading) { onTap() },
    ) {
        // ── Image or initials ─────────────────────────────────────────────────
        if (!imageURL.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = imageURL,
                contentDescription = "Profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                loading = { InitialsCircle(initials = initials, size = size) },
                error = { InitialsCircle(initials = initials, size = size) },
            )
        } else {
            InitialsCircle(initials = initials, size = size)
        }

        // ── Upload spinner overlay ────────────────────────────────────────────
        if (isUploading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f)),
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(size * 0.35f),
                )
            }
        }

        // ── Camera badge — bottom trailing ────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(24.dp)
                .clip(CircleShape)
                .background(PrimaryColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Change photo",
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

// ── Initials circle ───────────────────────────────────────────────────────────

@Composable
private fun InitialsCircle(
    initials: String,
    size: Dp,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(red = 0.08f, green = 0.18f, blue = 0.38f)),
    ) {
        Text(
            text = initials.ifBlank { "?" },
            fontSize = (size.value * 0.32f).sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AvatarViewPreview() {
    SuperAppZWTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            // With image
            AvatarView(
                imageURL = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=200",
                initials = "AM",
                onTap = {},
            )

            // Without image — initials fallback
            AvatarView(
                imageURL = null,
                initials = "AM",
                onTap = {},
            )

            // Uploading state
            AvatarView(
                imageURL = null,
                initials = "AM",
                isUploading = true,
                onTap = {},
            )

            // Large size
            AvatarView(
                imageURL = null,
                initials = "JD",
                size = 120.dp,
                onTap = {},
            )
        }
    }
}