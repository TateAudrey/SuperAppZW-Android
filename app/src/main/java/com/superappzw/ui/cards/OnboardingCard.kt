package com.superappzw.ui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.superappzw.model.OnboardingModel
import com.superappzw.model.events
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun OnboardingCard(
    model: OnboardingModel,
    cardWidth: Int,
    modifier: Modifier = Modifier // <- added
) {
    Box(
        modifier = modifier
            .width(cardWidth.dp)
            .height(380.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
    ) {
        // Background image
        Image(
            painter = painterResource(id = model.cardImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )

        // Bottom blur overlay with gradient mask
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomStart)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 24.dp,
                        bottomEnd = 24.dp
                    )
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.9f),
                            Color.White.copy(alpha = 1.0f),
                        )
                    )
                )
        )

        // Text overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.BottomStart),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = model.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = model.subtitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )


        }
    }
}


@Preview(showBackground = true)
@Composable
fun OnboardingCardPreview() {
    SuperAppZWTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            OnboardingCard(
                model = events.first(),
                cardWidth = 300
            )
        }
    }
}
