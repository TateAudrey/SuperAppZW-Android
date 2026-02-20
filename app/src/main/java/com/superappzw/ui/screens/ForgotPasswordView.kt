package com.superappzw.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.R
import com.superappzw.ui.components.buttons.PrimaryActionButton
import com.superappzw.ui.components.textfields.EmailOnlyField
import com.superappzw.ui.components.utils.AppAlert
import com.superappzw.ui.components.utils.LoadingView
import com.superappzw.ui.theme.FuturaMediumFamily
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme
import com.superappzw.viewModel.ForgotPasswordViewModel


@Composable
fun ForgotPasswordView(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = viewModel(),
    onForgotClick: () -> Unit = {},
    navigateBack: () -> Unit
) {

    val isFormValid by remember(viewModel.email) {
        derivedStateOf {
            viewModel.email.trim().isNotEmpty()
        }
    }
    SuperAppZWTheme {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp)
                    .blur(radius = if (viewModel.isLoading) 2.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Back Button - Top Left
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = navigateBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Go back",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(PrimaryColor)
                        )

                    }
                }

                // Title
                Text(
                    text = "Forgot Password?",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FuturaMediumFamily,
                    color = PrimaryColor
                )

                // Form fields

                EmailOnlyField(
                    text = viewModel.email,
                    onTextChange = { viewModel.email = it },
                    placeholder = "Email address",
                    modifier = Modifier.padding(top = 20.dp)
                )

                // Sign Up button
                PrimaryActionButton(
                    title = "Request Password Reset",
                    onClick = {
                        viewModel.requestPasswordReset(
                            navigateBack
                        )
                    },
                    enabled = !viewModel.isLoading && isFormValid,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            // Loading overlay
            if (viewModel.isLoading) {
                LoadingView()
            }

            // AppAlert
            AppAlert(
                alertType = viewModel.alertType,
                onDismiss = { viewModel.alertType = null }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordViewPreview() {
    ForgotPasswordView(
        navigateBack = { }
    )
}


