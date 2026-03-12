package com.superappzw.ui.accountStatus

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun TermsAcceptanceView(
    onAccepted: () -> Unit,
    viewModel: TermsViewModel = viewModel(),
) {
    val terms          by viewModel.terms.collectAsState()
    val isLoading      by viewModel.isLoading.collectAsState()
    val isAccepting    by viewModel.isAccepting.collectAsState()
    val errorMessage   by viewModel.errorMessage.collectAsState()
    val didAccept      by viewModel.didAccept.collectAsState()

    var hasScrolledToBottom by remember { mutableStateOf(false) }

    // Mirrors .task { await viewModel.load() }
    LaunchedEffect(Unit) { viewModel.load() }

    // Mirrors: if viewModel.didAccept { onAccepted() }
    LaunchedEffect(didAccept) {
        if (didAccept) onAccepted()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7)),
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp),
            ) {
                Text(
                    text = "Terms & Conditions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                terms?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Last updated ${it.lastUpdated}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE0E0E0))

            // ── Content ───────────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            CircularProgressIndicator(color = PrimaryColor)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Loading Terms…", color = Color.Gray)
                        }
                    }

                    errorMessage != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(36.dp),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Could not load Terms",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(onClick = { viewModel.load() }) {
                                Text("Try Again", color = PrimaryColor)
                            }
                        }
                    }

                    terms != null -> {
                        TermsWebView(
                            html = terms!!.html,
                            onScrolledToBottom = { hasScrolledToBottom = true },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }

        // ── Accept button — fixed at bottom, floats over content ──────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .shadow(elevation = 8.dp)
                .background(Color.White)
                .padding(top = 12.dp, bottom = 32.dp)
                .padding(horizontal = 20.dp),
        ) {
            AnimatedVisibility(
                visible = !hasScrolledToBottom,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = "Please scroll to the bottom to accept",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            Button(
                onClick = { viewModel.accept() },
                enabled = hasScrolledToBottom && !isAccepting,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                if (isAccepting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "I Accept the Terms & Conditions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

// ── Terms WebView ─────────────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun TermsWebView(
    html: String,
    onScrolledToBottom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Guard so the callback only fires once — mirrors Coordinator.hasTriggered
    var hasTriggered by remember { mutableStateOf(false) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.setSupportZoom(false)
                isVerticalScrollBarEnabled = true
                setBackgroundColor(android.graphics.Color.TRANSPARENT)

                // Scroll-to-bottom detection — mirrors UIScrollViewDelegate
                setOnScrollChangeListener { _, _, scrollY, _, _ ->
                    if (!hasTriggered) {
                        val contentHeight = (contentHeight * resources.displayMetrics.density).toInt()
                        val frameHeight = height
                        // Trigger within 60dp of the bottom — mirrors the 60pt threshold in Swift
                        if (scrollY + frameHeight >= contentHeight - (60 * resources.displayMetrics.density)) {
                            hasTriggered = true
                            onScrolledToBottom()
                        }
                    }
                }
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                null,
                styledHtml(html),
                "text/html",
                "UTF-8",
                null,
            )
        },
        modifier = modifier,
    )
}

private fun styledHtml(body: String): String = """
    <!DOCTYPE html>
    <html>
    <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <style>
      * { box-sizing: border-box; margin: 0; padding: 0; }
      body {
        font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
        font-size: 16px;
        line-height: 1.7;
        color: #1c1c1e;
        background-color: #f2f2f7;
        padding: 20px 20px 160px 20px;
      }
      h1 { font-size: 24px; font-weight: 700; margin-bottom: 6px; }
      .meta { font-size: 13px; color: #6e6e73; margin-bottom: 24px; display: block; }
      h2 { font-size: 17px; font-weight: 600; margin-bottom: 10px; }
      h3 { font-size: 15px; font-weight: 600; margin-top: 12px; margin-bottom: 6px; }
      p  { font-size: 15px; margin-bottom: 10px; }
      ul, ol { padding-left: 20px; margin-bottom: 10px; }
      li { font-size: 15px; margin-bottom: 5px; }
      .card { background: #ffffff; border-radius: 12px; padding: 16px 16px 8px 16px; margin-bottom: 12px; }
      strong { font-weight: 600; }
      .footer { text-align: center; font-size: 12px; color: #8e8e93; margin-top: 24px; line-height: 1.8; }
    </style>
    </head>
    <body>
    $body
    </body>
    </html>
""".trimIndent()

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TermsAcceptanceViewPreview() {
    SuperAppZWTheme {
        TermsAcceptanceView(onAccepted = {})
    }
}