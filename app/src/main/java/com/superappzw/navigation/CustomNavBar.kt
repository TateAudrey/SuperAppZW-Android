package com.superappzw.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.superappzw.ui.theme.GloriaHallelujah
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme


@Composable
fun CustomNavBar(
    title: String,
    profileImageURL: String?,
    userName: String = "",
    onProfileTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 8.dp),
    ) {
        // ── Title ─────────────────────────────────────────────────────────────
        Text(
            text = title,
            fontFamily = GloriaHallelujah,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.size(12.dp))

        // ── Profile circle ────────────────────────────────────────────────────
        ProfileCircle(
            profileImageURL = profileImageURL,
            userName = userName,
            onTap = onProfileTap,
        )
    }
}

// ── ProfileCircle ─────────────────────────────────────────────────────────────

@Composable
private fun ProfileCircle(
    profileImageURL: String?,
    userName: String,
    onTap: (() -> Unit)?,
) {
    val initial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: ""

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(42.dp)
            .shadow(elevation = 4.dp, shape = CircleShape, ambientColor = Color.Black.copy(alpha = 0.08f))
            .clip(CircleShape)
            .background(PrimaryColor)
            .then(
                if (onTap != null) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap,
                ) else Modifier
            ),
    ) {
        if (!profileImageURL.isNullOrBlank()) {
            // Loads remote image; falls back to placeholder on failure
            SubcomposeAsyncImage(
                model = profileImageURL,
                contentDescription = "Profile picture of $userName",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                error = {
                    PlaceholderContent(initial = initial)
                },
                loading = {
                    PlaceholderContent(initial = initial)
                },
            )
        } else {
            PlaceholderContent(initial = initial)
        }
    }
}

// ── Placeholder (initials or person icon) ─────────────────────────────────────

@Composable
private fun PlaceholderContent(initial: String) {
    if (initial.isNotEmpty()) {
        Text(
            text = initial,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    } else {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp),
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Nav Bar – with image", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun CustomNavBarWithImagePreview() {
    SuperAppZWTheme {
        CustomNavBar(
            title = "Hello, Tino",
            profileImageURL = "https://i.pravatar.cc/150",
            userName = "Tino",
            onProfileTap = {},
        )
    }
}

@Preview(name = "Nav Bar – initials fallback", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun CustomNavBarInitialsPreview() {
    SuperAppZWTheme {
        CustomNavBar(
            title = "Linjani, Tatenda",
            profileImageURL = null,
            userName = "Tino Moyo",
            onProfileTap = {},
        )
    }
}

@Preview(name = "Nav Bar – no user", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun CustomNavBarNoUserPreview() {
    SuperAppZWTheme {
        CustomNavBar(
            title = "Ndeip, Audrey",
            profileImageURL = null,
            userName = "",
            onProfileTap = {},
        )
    }
}