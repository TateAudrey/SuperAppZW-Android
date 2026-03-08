package com.superappzw.ui.lisitngs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import com.superappzw.ui.components.utils.AppAlertType
import com.superappzw.ui.components.utils.EmptyStateView

@Composable
fun ProductsTabView(
    viewModel: MyListingsViewModel,
    modifier: Modifier = Modifier,
) {
    val myProducts by viewModel.myProducts.collectAsState()
    val haptic = LocalHapticFeedback.current

    if (myProducts.isEmpty()) {
        EmptyStateView(
            icon = Icons.Outlined.Inbox,
            message = "You haven't posted any products yet",
            modifier = modifier,
        )
    } else {
        ListingsGridView(
            listings = myProducts,
            onTap = { itemCode, ownerUserID ->
                val listing = myProducts.firstOrNull { it.itemCode == itemCode }
                listing?.let { viewModel.setSelectedListing(it) }
            },
            onLongPress = { listing ->
                // Heavy haptic — mirrors UIImpactFeedbackGenerator(style: .heavy)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                viewModel.setAlertType(
                    AppAlertType.DeleteListing(
                        title = "Delete ${listing.title}?",
                        cancelAction = null,
                        deleteAction = { viewModel.deleteListing(listing) },
                    )
                )
            },
            modifier = modifier.fillMaxWidth(),
        )
    }
}