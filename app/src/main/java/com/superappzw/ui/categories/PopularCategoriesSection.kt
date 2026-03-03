package com.superappzw.ui.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun PopularCategoriesSection(
    categories: List<CategoryItem>,
    onSelect: ((CategoryItem) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    // Split into two rows — even indices top, odd indices bottom
    val topRow = categories.filterIndexed { index, _ -> index % 2 == 0 }
    val bottomRow = categories.filterIndexed { index, _ -> index % 2 != 0 }

    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 4.dp),
    ) {
        // ── Row 1 — even indexed items ────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            topRow.forEach { item ->
                CategoryPill(
                    item = item,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect?.invoke(item) },
                )
            }
        }

        // ── Row 2 — odd indexed items ─────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            bottomRow.forEach { item ->
                CategoryPill(
                    item = item,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect?.invoke(item) },
                )
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun PopularCategoriesSectionPreview() {
    SuperAppZWTheme {
        PopularCategoriesSection(
            categories = CategoryItem.all,
            onSelect = {},
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}