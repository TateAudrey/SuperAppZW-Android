package com.superappzw.ui.lisitngs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.superappzw.ui.components.utils.AppAlertType
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun ServicesTabView(
    viewModel: MyListingsViewModel,
    modifier: Modifier = Modifier,
) {
    val myServices by viewModel.myServices.collectAsState()

    var showAddService by remember { mutableStateOf(false) }
    var newServiceText by remember { mutableStateOf("") }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {

        // ── Main content ──────────────────────────────────────────────────────
        if (myServices.isEmpty()) {
            ServicesEmptyState(
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 120.dp),
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp)),
                    ) {
                        myServices.forEachIndexed { index, service ->
                            ServiceRow(
                                service = service,
                                onDelete = {
                                    viewModel.setAlertType(
                                        AppAlertType.DeleteListing(
                                            title = "Delete $service?",
                                            message = "This will permanently remove this service. This action cannot be undone.",
                                            cancelAction = null,
                                            deleteAction = { viewModel.deleteService(service) },
                                        )
                                    )
                                },
                            )
                            if (index < myServices.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = Color(0xFFF0F0F0),
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── FAB ───────────────────────────────────────────────────────────────
        FloatingActionButton(
            onClick = {
                newServiceText = ""
                showAddService = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp),
        )

        // ── Add service alert overlay ─────────────────────────────────────────
        // Mirrors .transition(.opacity.combined(with: .scale(scale: 0.95)))
        AnimatedVisibility(
            visible = showAddService,
            enter = fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.95f),
            exit = fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.95f),
            modifier = Modifier.zIndex(1f),
        ) {
            CustomTextFieldAlert(
                title = "Add Service",
                placeholder = "Service Name",
                text = newServiceText,
                onTextChange = { newServiceText = it },
                onCancel = {
                    showAddService = false
                    newServiceText = ""
                },
                onAdd = {
                    viewModel.addService(newServiceText.trim())
                    showAddService = false
                    newServiceText = ""
                },
            )
        }
    }
}

// ── Service row ───────────────────────────────────────────────────────────────

@Composable
private fun ServiceRow(
    service: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = service,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f),
        )
        // Swipe-to-delete isn't natively available in Compose — red trash button
        // is the idiomatic equivalent for a destructive row action
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete $service",
                tint = Color.Red,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun ServicesEmptyState(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(top = 80.dp, start = 40.dp, end = 40.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Build,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "You haven't posted any services yet",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ServicesTabViewPreview() {
    SuperAppZWTheme {
        // Preview uses a stub since ViewModel state is private
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(14.dp)),
            ) {
                listOf("Delivery person", "Gamer", "Software Engineer").forEachIndexed { index, service ->
                    ServiceRow(service = service, onDelete = {})
                    if (index < 2) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFF0F0F0),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun ServicesEmptyStatePreview() {
    SuperAppZWTheme {
        ServicesEmptyState()
    }
}