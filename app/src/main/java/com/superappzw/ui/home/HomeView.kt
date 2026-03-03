package com.superappzw.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.navigation.CustomNavBar
import com.superappzw.ui.components.buttons.PrimaryActionButton
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    profileImageURL: String? = null,
    userName: String = "",
    onProfileTap: (() -> Unit)? = null,
) {
    SuperAppZWTheme {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            // ── Nav bar ───────────────────────────────────────────────────────
            CustomNavBar(
                title = "Hello, Audrey",
                profileImageURL = profileImageURL,
                userName = userName,
                onProfileTap = onProfileTap,
            )

            // ── Screen content ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Hello, World!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryActionButton(
                    title = "Log out",
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Home – with initials", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun HomeViewInitialsPreview() {
    HomeView(
        onLogout = {},
        profileImageURL = null,
        userName = "Tino Moyo",
    )
}

