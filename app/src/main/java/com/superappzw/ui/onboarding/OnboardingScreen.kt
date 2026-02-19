package com.superappzw.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.ui.buttons.PrimaryActionButton
import com.superappzw.ui.utils.OnboardingCarousel
import com.superappzw.R.drawable

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo

        Image(
            painter = painterResource(id = drawable.logo_text),
            contentDescription = "App Logo",
            modifier = Modifier
                .padding(top = 32.dp)  // Top padding added
                .size(150.dp, 100.dp)
                .clip(RoundedCornerShape(15.dp))
        )


        // Carousel
        OnboardingCarousel(
            cardWidth = 260,
            spacing = 24
        )

        // Tagline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "A hub that connects you to your clients",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        // CTA Button (does nothing for now)
        PrimaryActionButton(
            title = "Get Started",
            onClick = onGetStartedClick,
            modifier = Modifier
                .padding(horizontal = 32.dp)  // <-- add horizontal padding
        )


        Spacer(modifier = Modifier.height(32.dp))
    }
}


