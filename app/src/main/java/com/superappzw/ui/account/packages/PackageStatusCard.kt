package com.superappzw.ui.account.packages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun PackageStatusCard(
    modifier: Modifier = Modifier,
    viewModel: PackageStatusViewModel = viewModel(),
) {
    val currentPackage    by viewModel.currentPackage.collectAsState()
    val isLoading         by viewModel.isLoading.collectAsState()
    val errorMessage      by viewModel.errorMessage.collectAsState()
    val activationSuccess by viewModel.activationSuccess.collectAsState()
    val expiryBadgeColor  by viewModel.expiryBadgeColor.collectAsState()
    val expiryLabel       by viewModel.expiryLabel.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(16.dp),
    ) {
        when {

            // ── Loading ───────────────────────────────────────────────────────
            isLoading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Loading package info...",
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )
                }
            }

            // ── Content ───────────────────────────────────────────────────────
            currentPackage != null -> {
                val info = currentPackage!!

                // ── Package name + expiry badge ───────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = info.packageName.uppercase(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                        )
                        Text(
                            text = "Current Package",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    }

                    // Expiry badge — hidden for standard
                    if (!info.isStandard) {
                        Text(
                            text = expiryLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(expiryBadgeColor)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF0F0F0))

                // ── Limit columns ─────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    LimitColumn(
                        icon = Icons.Filled.Sell,
                        label = "Products",
                        value = "${info.maxProducts}",
                        modifier = Modifier.weight(1f),
                    )
                    VerticalDivider(color = Color(0xFFF0F0F0))
                    LimitColumn(
                        icon = Icons.Filled.Build,
                        label = "Services",
                        value = "${info.maxServices}",
                        modifier = Modifier.weight(1f),
                    )
                    if (!info.isStandard) {
                        VerticalDivider(color = Color(0xFFF0F0F0))
                        LimitColumn(
                            icon = Icons.Filled.CalendarMonth,
                            label = "Valid Days",
                            value = "${info.validDays}",
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                // ── Expiry date row ───────────────────────────────────────────
                if (info.formattedExpiry != null) {
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Expires ${info.formattedExpiry}",
                            fontSize = 13.sp,
                            color = Color.Gray,
                        )
                    }
                }
            }
        }
    }

    // ── Error alert ───────────────────────────────────────────────────────────
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Error", style = MaterialTheme.typography.headlineSmall) },
            text  = { Text(errorMessage ?: "", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) { Text("OK") }
            },
        )
    }

    // ── Activation success alert ──────────────────────────────────────────────
    if (activationSuccess != null) {
        val result = activationSuccess!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissSuccess() },
            title = { Text("Package Activated!", style = MaterialTheme.typography.headlineSmall) },
            text  = {
                Text(
                    "${result.packageID.replaceFirstChar { it.uppercase() }} package is now " +
                            "active until ${result.formattedExpiry}.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissSuccess() }) { Text("OK") }
            },
        )
    }
}

// ── Limit column ──────────────────────────────────────────────────────────────

@Composable
private fun LimitColumn(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray,
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun PackageStatusCardPreview() {
    SuperAppZWTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PackageStatusCard()
        }
    }
}