package com.superappzw.ui.lisitngs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun CustomTextFieldAlert(
    title: String,
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    onCancel: () -> Unit,
    onAdd: () -> Unit,
) {
    // Dialog fills the screen and dims behind — mirrors ZStack with Color.black.opacity(0.4)
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCancel,
                ),
        ) {
            // ── Alert card ────────────────────────────────────────────────────
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(40.dp),
                        ambientColor = Color.Black.copy(alpha = 0.15f),
                        spotColor = Color.Black.copy(alpha = 0.15f),
                    )
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.White)
                    .padding(24.dp)
                    // Consume clicks so taps on the card don't dismiss
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                // ── Title ─────────────────────────────────────────────────────
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                // ── Text field ────────────────────────────────────────────────
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(text = placeholder, color = Color(0xFFBDBDBD))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF2F2F7),
                        unfocusedContainerColor = Color(0xFFF2F2F7),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                // ── Buttons ───────────────────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Cancel
                    Button(
                        onClick = onCancel,
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE5E5EA),
                            contentColor = Color.Red,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    // Add — disabled when text is blank
                    Button(
                        onClick = {
                            if (text.isNotBlank()) onAdd()
                        },
                        enabled = text.isNotBlank(),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFBDBDBD),
                            disabledContentColor = Color.White,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                    ) {
                        Text(
                            text = "Add",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7, showSystemUi = true)
@Composable
private fun CustomTextFieldAlertPreview() {
    SuperAppZWTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F7)),
        ) {
            CustomTextFieldAlert(
                title = "Add Service",
                placeholder = "Service Name",
                text = "",
                onTextChange = {},
                onCancel = {},
                onAdd = {},
            )
        }
    }
}