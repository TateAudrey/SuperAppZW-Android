package com.superappzw.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import com.superappzw.ui.theme.PrimaryColor

// ── Guest Prompt Reason ───────────────────────────────────────────────────────

enum class GuestPromptReason {
    MY_LISTINGS,
    FAVOURITES,
    CONTACT,
    REVIEW,
    ACCOUNT;

    val title: String
        get() = when (this) {
            MY_LISTINGS -> "Sign In to Post Listings"
            FAVOURITES  -> "Sign In to Save Favourites"
            CONTACT     -> "Sign In to Contact Sellers"
            REVIEW      -> "Sign In to Write a Review"
            ACCOUNT     -> "Sign In to Access Your Account"
        }

    val message: String
        get() = when (this) {
            MY_LISTINGS -> "Create an account to list your products and services and reach customers across Zimbabwe."
            FAVOURITES  -> "Sign in to save listings you love and find them again easily."
            CONTACT     -> "Sign in to contact sellers directly via WhatsApp about their products and services."
            REVIEW      -> "Sign in to share your experience and help other customers make informed decisions."
            ACCOUNT     -> "Create an account to manage your listings, track favourites and personalise your experience."
        }

    val icon: ImageVector
        get() = when (this) {
            MY_LISTINGS -> Icons.Filled.List
            FAVOURITES  -> Icons.Filled.Favorite
            CONTACT     -> Icons.Filled.Phone
            REVIEW      -> Icons.Filled.Star
            ACCOUNT     -> Icons.Filled.Person
        }
}

// ── Guest Sign In Prompt Sheet ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestSignInPromptSheet(
    reason: GuestPromptReason,
    onSignIn: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            // ── Icon ──────────────────────────────────────────────────────────
            Icon(
                imageVector = reason.icon,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(40.dp),
            )

            // ── Text ──────────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = reason.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                )
                Text(
                    text = reason.message,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
            }

            // ── Sign In button ────────────────────────────────────────────────
            Button(
                onClick = {
                    onDismiss()
                    onSignIn()
                },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    text = "Sign In / Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }

            // ── Continue Browsing button ──────────────────────────────────────
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Continue Browsing",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor,
                )
            }
        }
    }
}