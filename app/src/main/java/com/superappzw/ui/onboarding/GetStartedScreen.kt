package com.superappzw.ui.onboarding

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.R
import com.superappzw.ui.components.buttons.SocialButton
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun GetStartedScreen(
    modifier: Modifier = Modifier,
    navigateToEmail: () -> Unit,
    navigateToGoogle: () -> Unit,
    navigateBack: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
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

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_text),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp, 100.dp)
                    .clip(RoundedCornerShape(15.dp))
            )

            // Title
            Text(
                text = "Get Started",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )

            // Email Button
            SocialButton(
                title = "Continue with email address",
                icon = painterResource(id = R.drawable.ic_email),
                onClick = navigateToEmail
            )

            // Google Button
            SocialButton(
                title = "Continue with Google",
                icon = painterResource(id = R.drawable.ic_google),
                onClick = navigateToGoogle
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
fun GetStartedScreenPreview() {
    SuperAppZWTheme {
        GetStartedScreen(
            modifier = Modifier.fillMaxSize(),
            navigateToEmail = { },
            navigateToGoogle = { },
            navigateBack = { }
        )
    }
}

