package com.superappzw.ui.accountStatus

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun SuspendedAccountView() {
    val context = LocalContext.current

    GateView(
        title = "Account Suspended",
        message = "Your account has been temporarily suspended due to a violation of our policies or unusual activity detected on the account. This action helps us maintain a safe and secure environment for all users. If you believe this suspension was made in error or would like more information, please contact our support team for assistance.",
        icon = Icons.Filled.Block,
        iconTint = Color.Red,
        buttonLabel = "Contact Support",
        rotatesIcon = false,
        onButton = {
            val phone = "264818477316"
            val message = "Hi, my Super App ZW account has been suspended and I'd like more information."
            val encoded = Uri.encode(message)
            val url = Uri.parse("https://wa.me/$phone?text=$encoded")
            context.startActivity(Intent(Intent.ACTION_VIEW, url))
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SuspendedAccountViewPreview() {
    SuperAppZWTheme {
        SuspendedAccountView()
    }
}