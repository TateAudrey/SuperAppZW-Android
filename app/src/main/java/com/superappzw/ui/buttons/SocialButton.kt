package com.superappzw.ui.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superappzw.R
import com.superappzw.ui.theme.PrimaryColor

@Composable
fun SocialButton(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(30.dp))
            .border(1.5.dp, PrimaryColor, RoundedCornerShape(30.dp)) // AccentColor equivalent
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp, 30.dp)
            )
            Text(
                text = title,
                fontSize = 16.sp,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, // Futura equivalent
                fontWeight = FontWeight.Normal,
                color = PrimaryColor
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SocialButtonsPreview() {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SocialButton(
            title = "Continue with email address",
            icon = painterResource(id = R.drawable.ic_email),
            onClick = { }
        )

        SocialButton(
            title = "Continue with Google",
            icon = painterResource(id = R.drawable.ic_google),
            onClick = { }
        )
    }
}
