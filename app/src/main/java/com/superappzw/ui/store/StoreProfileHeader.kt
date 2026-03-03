package com.superappzw.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

private val AvatarBackground = Color(0xFF142E61)
private val AvatarShape = RoundedCornerShape(10.dp)

@Composable
fun StoreProfileHeader(
    storeName: String,
    ownerName: String,
    suburb: String,
    location: String,
    profileImageURL: String?,
    modifier: Modifier = Modifier,
) {
    val initials = buildInitials(ownerName)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
    ) {

        // ── Avatar ────────────────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(70.dp)
                .clip(AvatarShape)
                .background(AvatarBackground),
        ) {
            if (!profileImageURL.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = profileImageURL,
                    contentDescription = "Profile image of $ownerName",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(AvatarShape),
                    loading = {
                        InitialsContent(initials = initials)
                    },
                    error = {
                        InitialsContent(initials = initials)
                    },
                )
            } else {
                InitialsContent(initials = initials)
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // ── Details ───────────────────────────────────────────────────────────
        Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
            Text(
                text = ownerName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Suburb: ") }
                    append(suburb)
                },
                fontSize = 13.sp,
                color = Color.Gray,
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Location: ") }
                    append(location)
                },
                fontSize = 13.sp,
                color = Color.Gray,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

// ── Initials helper ───────────────────────────────────────────────────────────

private fun buildInitials(ownerName: String): String {
    val parts = ownerName.trim().split(" ")
    val first = parts.firstOrNull()?.take(1)?.uppercase() ?: ""
    val last = parts.drop(1).firstOrNull()?.take(1)?.uppercase() ?: ""
    return "$first$last"
}

@Composable
private fun InitialsContent(initials: String) {
    Text(
        text = initials,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Store Header – with image", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun StoreProfileHeaderWithImagePreview() {
    SuperAppZWTheme {
        StoreProfileHeader(
            storeName = "Chaks Foods",
            ownerName = "Mrs Matthews",
            suburb = "Tynwald North",
            location = "Harare",
            profileImageURL = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=200",
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}

@Preview(name = "Store Header – initials fallback", showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun StoreProfileHeaderNoImagePreview() {
    SuperAppZWTheme {
        StoreProfileHeader(
            storeName = "Chaks Foods",
            ownerName = "Mrs Matthews",
            suburb = "Tynwald North",
            location = "Harare",
            profileImageURL = null,
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}