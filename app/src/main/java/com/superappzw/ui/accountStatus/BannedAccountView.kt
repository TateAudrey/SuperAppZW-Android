package com.superappzw.ui.accountStatus

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun BannedAccountView() {
    val context = LocalContext.current

    GateView(
        title = "Account Banned",
        message = "Your account has been permanently banned due to a violation of our Terms and Conditions or community guidelines. This action was taken to maintain a safe and trusted environment for all users. If you believe this decision was made in error, you may contact our support team for further review.",
        icon = Icons.Filled.PanTool,
        iconTint = Color.Red,
        buttonLabel = "Contact Support",
        rotatesIcon = false,
        onButton = {
            val phone = "264818477316"
            val message = "Hi, my Super App ZW account has been banned and I'd like to appeal this decision."
            val encoded = Uri.encode(message)
            val url = Uri.parse("https://wa.me/$phone?text=$encoded")
            context.startActivity(Intent(Intent.ACTION_VIEW, url))
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BannedAccountViewPreview() {
    SuperAppZWTheme {
        BannedAccountView()
    }
}