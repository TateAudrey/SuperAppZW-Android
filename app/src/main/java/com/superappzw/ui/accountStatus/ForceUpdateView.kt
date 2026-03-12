package com.superappzw.ui.accountStatus

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

private const val PLAY_STORE_PACKAGE = "com.superappzw.app" // replace with real package name

@Composable
fun ForceUpdateView() {
    val context = LocalContext.current

    GateView(
        title = "Update App",
        message = "A new version of the app is available! This update brings important improvements, new capabilities, and fixes that help the app run more smoothly and securely. We recommend updating to the latest version to continue enjoying the best experience possible.",
        icon = Icons.Filled.Settings,
        iconTint = PrimaryColor,
        buttonLabel = "Update",
        rotatesIcon = true,
        onButton = {
            // Try opening the Play Store app first; fall back to browser if not installed
            val playStoreApp = Uri.parse("market://details?id=$PLAY_STORE_PACKAGE")
            val playStoreBrowser = Uri.parse("https://play.google.com/store/apps/details?id=$PLAY_STORE_PACKAGE")
            val intent = Intent(Intent.ACTION_VIEW, playStoreApp).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            runCatching { context.startActivity(intent) }
                .onFailure { context.startActivity(Intent(Intent.ACTION_VIEW, playStoreBrowser)) }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ForceUpdateViewPreview() {
    SuperAppZWTheme {
        ForceUpdateView()
    }
}