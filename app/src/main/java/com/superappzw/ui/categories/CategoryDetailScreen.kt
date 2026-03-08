package com.superappzw.ui.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CategoryDetailScreen(category: CategoryItem) {
    val viewModel: CategoryDetailViewModel = viewModel(
        factory = CategoryDetailViewModelFactory(category)
    )

    LaunchedEffect(Unit) {
        viewModel.load()
    }
}