package com.superappzw.ui.reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.model.MyReviewModel
import com.superappzw.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReviewsView(
    modifier: Modifier = Modifier,
    viewModel: MyReviewsViewModel = viewModel(),
) {
    val items     by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Reload every time the screen becomes visible
    // Mirrors Swift's .onAppear { Task { await viewModel.load() } }
    DisposableEffect(Unit) {
        viewModel.load()
        onDispose { }
    }

    // Pending delete held until user confirms
    var pendingDelete by remember { mutableStateOf<MyReviewModel?>(null) }

    Scaffold(
        modifier       = modifier,
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "My Reviews",
                        fontWeight = FontWeight.SemiBold,
                        color      = PrimaryColor,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { innerPadding ->

        when {
            // ── Loading ───────────────────────────────────────────────────────
            isLoading && items.isEmpty() -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }

            // ── Empty state ───────────────────────────────────────────────────
            items.isEmpty() -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 40.dp),
                ) {
                    Icon(
                        imageVector    = Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint           = Color(0xFFBDBDBD),
                        modifier       = Modifier.size(52.dp),
                    )
                    Text(
                        text       = "No Reviews Yet",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(top = 16.dp),
                    )
                    Text(
                        text      = "Reviews you leave on stores will appear here.",
                        fontSize  = 14.sp,
                        color     = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.padding(top = 8.dp),
                    )
                }
            }

            // ── List ──────────────────────────────────────────────────────────
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding      = PaddingValues(
                        start  = 16.dp,
                        end    = 16.dp,
                        top    = 16.dp,
                        bottom = 30.dp,
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    items(items, key = { it.id }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    pendingDelete = item
                                }
                                // Return false — row snaps back, dialog confirms before delete
                                false
                            },
                        )

                        SwipeToDismissBox(
                            state                    = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                Box(
                                    contentAlignment = Alignment.CenterEnd,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(end = 20.dp),
                                ) {
                                    Icon(
                                        imageVector    = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint           = Color.Red,
                                        modifier       = Modifier.size(24.dp),
                                    )
                                }
                            },
                        ) {
                            MyReviewRow(item = item)
                        }
                    }
                }
            }
        }
    }

    // ── Delete confirmation dialog ────────────────────────────────────────────
    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            icon = {
                Icon(
                    imageVector    = Icons.Filled.Delete,
                    contentDescription = null,
                    tint           = Color.Red,
                )
            },
            title = { Text("Delete Review") },
            text  = { Text("Are you sure you want to delete your review? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(item)
                    pendingDelete = null
                }) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}