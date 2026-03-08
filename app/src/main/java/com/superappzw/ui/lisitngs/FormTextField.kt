package com.superappzw.ui.lisitngs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun FormTextField(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isDisabled: Boolean = false,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = !isDisabled,
        placeholder = {
            Text(text = placeholder, fontSize = 15.sp, color = Color(0xFFBDBDBD))
        },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color(0xFFF2F2F7).copy(alpha = 0.5f),
        ),
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isDisabled) 0.4f else 1f)
            .shadow(
                elevation = if (isDisabled) 0.dp else 4.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f),
            ),
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun FormTextFieldPreview() {
    SuperAppZWTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            FormTextField(
                placeholder = "Title",
                value = "",
                onValueChange = {},
            )
            FormTextField(
                placeholder = "Disabled Field",
                value = "Locked value",
                onValueChange = {},
                isDisabled = true,
            )
        }
    }
}