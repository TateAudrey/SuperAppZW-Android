package com.superappzw.appPolicy

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPoliciesView(
    viewModel: PolicyViewModel = viewModel(),
) {
    val policy       by viewModel.policy.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Policies & Guidelines",
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {

                // ── Loading ───────────────────────────────────────────────────
                isLoading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            modifier = Modifier.size(32.dp),
                        )
                        Text(
                            text = "Loading policies...",
                            fontSize = 15.sp,
                            color = Color.Gray,
                        )
                    }
                }

                // ── Error ─────────────────────────────────────────────────────
                errorMessage != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "Could not load policies",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                        )
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                        )
                        Button(onClick = { viewModel.load() }) {
                            Text("Try Again", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // ── Empty HTML ────────────────────────────────────────────────
                policy != null && policy!!.html.isBlank() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "No content found",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                        )
                        Text(
                            text = "The policy document exists but has no content. Check the html field in Firestore.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                // ── WebView ───────────────────────────────────────────────────
                policy != null -> {
                    PolicyWebView(
                        html = policy!!.html,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // ── Fallback (nil, no error, not loading) ─────────────────────
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "Nothing to show",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                        )
                        Text(
                            text = "Policy document not found. Make sure the collection is 'policies' and document ID is 'app_policies'.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                        )
                        Button(onClick = { viewModel.load() }) {
                            Text("Try Again", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// ── WebView ───────────────────────────────────────────────────────────────────
// Mirrors Swift's PolicyWebView (UIViewRepresentable) — renders HTML with
// the same CSS styling applied via a styledHTML wrapper.

@Composable
private fun PolicyWebView(
    html: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
                settings.useWideViewPort   = true
                settings.loadWithOverviewMode = true
                isVerticalScrollBarEnabled = true
                isHorizontalScrollBarEnabled = false
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
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

// ── HTML wrapper — mirrors Swift's styledHTML() ────────────────────────────────

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
        padding: 20px 20px 60px 20px;
      }
      h1 { font-size: 24px; font-weight: 700; margin-bottom: 6px; }
      .meta { font-size: 13px; color: #6e6e73; margin-bottom: 24px; display: block; }
      h2 { font-size: 17px; font-weight: 600; margin-bottom: 10px; }
      h3 { font-size: 15px; font-weight: 600; margin-top: 12px; margin-bottom: 6px; }
      p  { font-size: 15px; margin-bottom: 10px; }
      ul, ol { padding-left: 20px; margin-bottom: 10px; }
      li { font-size: 15px; margin-bottom: 5px; }
      .card {
        background: #ffffff;
        border-radius: 12px;
        padding: 16px 16px 8px 16px;
        margin-bottom: 12px;
      }
      strong { font-weight: 600; }
      .footer {
        text-align: center;
        font-size: 12px;
        color: #8e8e93;
        margin-top: 24px;
        line-height: 1.8;
      }
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
private fun AppPoliciesViewPreview() {
    SuperAppZWTheme {
        AppPoliciesView()
    }
}