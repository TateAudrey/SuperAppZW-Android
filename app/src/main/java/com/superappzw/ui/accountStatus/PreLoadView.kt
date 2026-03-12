package com.superappzw.ui.accountStatus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.superappzw.R
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun PreLoadView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {

        // ── Top doodle ────────────────────────────────────────────────────────
        Image(
            painter = painterResource(id = R.drawable.bottom_doodle),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter),
        )

        // ── Centered logo ─────────────────────────────────────────────────────
        Image(
            painter = painterResource(id = R.drawable.logo_text),
            contentDescription = "Super App ZW",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 100.dp)
                .align(Alignment.Center),
        )

        // ── Bottom doodle ─────────────────────────────────────────────────────
        Image(
            painter = painterResource(id = R.drawable.bottom_doodle),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreLoadViewPreview() {
    SuperAppZWTheme {
        PreLoadView()
    }
}