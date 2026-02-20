package com.superappzw.ui.components.textfields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.theme.FuturaBookFamily
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun EmailOnlyField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf(text) }

    // Filter to only allow email-safe characters
    LaunchedEffect(inputText) {
        val allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@._-"
        val filtered = inputText.filter { allowedCharacters.contains(it) }
        if (filtered != inputText) {
            onTextChange(filtered)
        }
    }

    OutlinedTextField(
        value = inputText,
        onValueChange = { newValue ->
            inputText = newValue
        },
        placeholder = {
            Text(
                text = placeholder,
                fontFamily = FuturaBookFamily,
                fontSize = 16.sp
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(30.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = PrimaryColor.copy(alpha = 0.5f),
            focusedLabelColor = PrimaryColor,
            cursorColor = PrimaryColor
        ),
        textStyle = TextStyle(
            fontFamily = FuturaBookFamily,
            fontSize = 16.sp
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { /* Handle done */ }
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
fun EmailOnlyFieldPreview() {
    SuperAppZWTheme {
        var email by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EmailOnlyField(
                text = email,
                onTextChange = { email = it },
                placeholder = "Enter your email address"
            )
        }
    }
}

