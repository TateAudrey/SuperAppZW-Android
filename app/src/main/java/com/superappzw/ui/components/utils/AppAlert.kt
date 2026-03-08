package com.superappzw.ui.components.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun AppAlert(
    alertType: AppAlertType?,
    onDismiss: () -> Unit,
) {
    if (alertType != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = when (alertType) {
                        is AppAlertType.Info          -> alertType.title
                        is AppAlertType.Confirm       -> alertType.title
                        is AppAlertType.SignOut        -> alertType.title
                        is AppAlertType.DeleteAccount -> alertType.title
                        is AppAlertType.DeleteListing -> alertType.title
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            text = {
                Text(
                    text = when (alertType) {
                        is AppAlertType.Info          -> alertType.message
                        is AppAlertType.Confirm       -> alertType.message
                        is AppAlertType.SignOut        -> alertType.message
                        is AppAlertType.DeleteAccount -> alertType.message
                        is AppAlertType.DeleteListing -> alertType.message
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                when (alertType) {
                    is AppAlertType.Info -> {
                        TextButton(onClick = {
                            alertType.dismissAction?.invoke()
                            onDismiss()
                        }) {
                            Text("Dismiss")
                        }
                    }
                    is AppAlertType.Confirm -> {
                        TextButton(onClick = {
                            alertType.proceedAction?.invoke()
                            onDismiss()
                        }) {
                            Text("Proceed")
                        }
                    }
                    is AppAlertType.SignOut -> {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                            onClick = {
                                alertType.signOutAction?.invoke()
                                onDismiss()
                            },
                        ) {
                            Text("Sign Out")
                        }
                    }
                    is AppAlertType.DeleteAccount -> {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                            onClick = {
                                alertType.deleteAction?.invoke()
                                onDismiss()
                            },
                        ) {
                            Text("Delete")
                        }
                    }
                    is AppAlertType.DeleteListing -> {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                            onClick = {
                                alertType.deleteAction?.invoke()
                                onDismiss()
                            },
                        ) {
                            Text("Delete")
                        }
                    }
                }
            },
            dismissButton = {
                when (alertType) {
                    is AppAlertType.Info -> {}
                    is AppAlertType.Confirm -> {
                        TextButton(onClick = {
                            alertType.cancelAction?.invoke()
                            onDismiss()
                        }) {
                            Text("Cancel")
                        }
                    }
                    is AppAlertType.SignOut -> {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                    }
                    is AppAlertType.DeleteAccount -> {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                    }
                    is AppAlertType.DeleteListing -> {
                        TextButton(onClick = {
                            alertType.cancelAction?.invoke()
                            onDismiss()
                        }) {
                            Text("Cancel")
                        }
                    }
                }
            },
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun AppAlertPreview() {
    SuperAppZWTheme {
        var alert by remember { mutableStateOf<AppAlertType?>(null) }
        Column {
            Button(onClick = {
                alert = AppAlertType.Confirm(
                    title = "Confirm Action",
                    message = "Are you sure?",
                    cancelAction = {},
                    proceedAction = {},
                )
            }) {
                Text("Show Confirm")
            }
            Button(onClick = {
                alert = AppAlertType.DeleteListing(
                    title = "Delete AirPods Pro?",
                    cancelAction = {},
                    deleteAction = {},
                )
            }) {
                Text("Show Delete Listing")
            }
            AppAlert(alertType = alert) { alert = null }
        }
    }
}
