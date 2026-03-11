package com.superappzw.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.superappzw.ui.account.packages.PackageHeaderCard
import com.superappzw.ui.components.utils.AppAlert
import com.superappzw.ui.components.utils.AppAlertType
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountView(
    navController: NavController,
    viewModel: AccountViewModel = viewModel(),
) {
    val profile by viewModel.profile.collectAsState()
    val alertType by viewModel.alertType.collectAsState()

    // Mirrors .task { await viewModel.load() }
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Account",
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { innerPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 40.dp),
        ) {

            // ── Profile header ────────────────────────────────────────────────
            AccountSection {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    AvatarView(
                        imageURL = profile?.profileImageURL,
                        initials = profile?.initials ?: "U",
                        size = 56.dp,
                        onTap = {},
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = profile?.fullName ?: "User",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                        )
                        Text(
                            text = profile?.phoneNumber ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                }
            }

            // ── Active package ────────────────────────────────────────────────
            AccountSection(title = "ACTIVE PACKAGE") {
                PackageHeaderCard(
                    packageName = profile?.packageID
                        ?.replaceFirstChar { it.uppercase() } ?: "Standard",
                    validDays = "Expires in 7 days",
                    modifier = Modifier.padding(12.dp),
                )
            }

            // ── My account ────────────────────────────────────────────────────
            AccountSection(title = "MY ACCOUNT") {
                AccountNavRow(
                    icon = Icons.Filled.AutoAwesome,
                    label = "Account Details",
                    onClick = { navController.navigate("profileDetail") },
                )
                SectionDivider()
                AccountNavRow(
                    icon = Icons.Filled.Lightbulb,
                    label = "Activate Premium",
                    onClick = { /* TODO */ },
                )
                SectionDivider()
                AccountNavRow(
                    icon = Icons.Filled.Description,
                    label = "App Policies & Guidelines",
                    onClick = { /* TODO */ },
                )
                SectionDivider()
                AccountNavRow(
                    icon = Icons.Filled.Headphones,
                    label = "Support",
                    onClick = { /* TODO */ },
                )
            }

            // ── Sign out ──────────────────────────────────────────────────────
            AccountSection {
                TextButton(
                    onClick = {
                        viewModel.setAlertType(
                            AppAlertType.SignOut(
                                signOutAction = { viewModel.signOut() }
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                ) {
                    Text(
                        text = "Sign Out",
                        color = Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            // ── Delete account — borderless, centered, muted ──────────────────
            // Mirrors .listRowBackground(Color.clear).listRowSeparator(.hidden)
            TextButton(
                onClick = {
                    viewModel.setAlertType(
                        AppAlertType.DeleteAccount(
                           // deleteAction = { viewModel.deleteAccount() }
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Delete Account",
                    fontSize = 14.sp,
                    color = Color(0xFFBDBDBD),
                )
            }
        }
    }

    // Rendered outside Scaffold so it floats above all content
    AppAlert(
        alertType = alertType,
        onDismiss = viewModel::dismissAlert,
    )
}

// ── Account section ───────────────────────────────────────────────────────────

@Composable
private fun AccountSection(
    title: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (title != null) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp)),
        ) {
            content()
        }
    }
}

// ── Nav row ───────────────────────────────────────────────────────────────────

@Composable
private fun AccountNavRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(20.dp),
        )
    }
}

// ── Section divider ───────────────────────────────────────────────────────────

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = Color(0xFFF0F0F0),
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AccountViewPreview() {
    SuperAppZWTheme {
        AccountView(navController = rememberNavController())
    }
}