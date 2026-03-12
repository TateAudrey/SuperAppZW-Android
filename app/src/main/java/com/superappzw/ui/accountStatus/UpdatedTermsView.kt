package com.superappzw.ui.accountStatus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun UpdatedTermsView(
    onAcknowledged: () -> Unit,
) {
    var showTerms by remember { mutableStateOf(false) }

    GateView(
        title = "Updated Ts & Cs",
        message = "We've updated our Terms and Conditions to provide clearer information about how our app works and how we protect our users. Please review the updated terms to stay informed. By continuing to use the app, you acknowledge and agree to the new Terms and Conditions.",
        icon = Icons.Filled.Description,
        iconTint = PrimaryColor,
        buttonLabel = "Read",
        rotatesIcon = false,
        onButton = { showTerms = true },
    )

    // Mirrors .fullScreenCover — renders TermsAcceptanceView on top when showTerms = true
    if (showTerms) {
        TermsAcceptanceView(
            onAccepted = {
                showTerms = false
                onAcknowledged()
            },
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun UpdatedTermsViewPreview() {
    SuperAppZWTheme {
        UpdatedTermsView(onAcknowledged = {})
    }
}
