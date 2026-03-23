package com.superappzw.ui.store

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.StoreReviewModel
import com.superappzw.ui.components.utils.EmptyStateView
import com.superappzw.ui.lisitngs.ListingsGridView
import com.superappzw.ui.onboarding.GuestPromptReason
import com.superappzw.ui.reviews.PostReviewSheet
import com.superappzw.ui.reviews.ReviewViewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreProfileView(
    storeID: String,
    onNavigateToListing: (StoreListing) -> Unit = {},
    isGuest: Boolean = false,
    onGuestSignInRequired: ((GuestPromptReason) -> Unit)? = null,
    storeProfileViewModel: StoreProfileViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
) {
    val currentUserID = try { FirebaseAuth.getInstance().currentUser?.uid } catch (e: Exception) { null }

    val storeName       by storeProfileViewModel.storeName.collectAsState()
    val ownerName       by storeProfileViewModel.ownerName.collectAsState()
    val suburb          by storeProfileViewModel.suburb.collectAsState()
    val location        by storeProfileViewModel.location.collectAsState()
    val profileImageURL by storeProfileViewModel.profileImageURL.collectAsState()
    val ownerUID        by storeProfileViewModel.ownerUID.collectAsState()
    val products        by storeProfileViewModel.products.collectAsState()
    val services        by storeProfileViewModel.services.collectAsState()
    val isLoading       by storeProfileViewModel.isLoading.collectAsState()
    val phoneNumber     by storeProfileViewModel.phoneNumber.collectAsState()

    val hasReviewed        by reviewViewModel.hasReviewed.collectAsState()
    val isSubmitting       by reviewViewModel.isSubmitting.collectAsState()
    val submitSuccess      by reviewViewModel.submitSuccess.collectAsState()
    val reviewErrorMessage by reviewViewModel.errorMessage.collectAsState()
    val reviews            by reviewViewModel.reviews.collectAsState()
    val averageRating      by reviewViewModel.averageRating.collectAsState()
    val totalReviews       by reviewViewModel.totalReviews.collectAsState()

    LaunchedEffect(storeID) {
        storeProfileViewModel.load(storeID)
        reviewViewModel.load(storeID)
    }

    StoreProfileContent(
        storeID = storeID,
        currentUserID = currentUserID,
        storeName = storeName,
        ownerName = ownerName,
        suburb = suburb,
        location = location,
        profileImageURL = profileImageURL,
        ownerUID = ownerUID,
        products = products,
        services = services,
        isLoading = isLoading,
        phoneNumber = phoneNumber,
        hasReviewed = hasReviewed,
        isSubmitting = isSubmitting,
        submitSuccess = submitSuccess,
        reviewErrorMessage = reviewErrorMessage,
        reviews = reviews,
        averageRating = averageRating,
        totalReviews = totalReviews,
        isGuest = isGuest,
        onGuestSignInRequired = onGuestSignInRequired,
        onNavigateToListing = onNavigateToListing,
        onSubmitReview = { rating, comment ->
            reviewViewModel.submitReview(
                storeOwnerUID = ownerUID,
                comment = comment,
                rating = rating,
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreProfileContent(
    storeID: String,
    currentUserID: String?,
    storeName: String,
    ownerName: String,
    suburb: String,
    location: String,
    profileImageURL: String?,
    ownerUID: String,
    products: List<StoreListing>,
    services: List<String>,
    isLoading: Boolean,
    phoneNumber: String?,
    hasReviewed: Boolean,
    isSubmitting: Boolean,
    submitSuccess: Boolean,
    reviewErrorMessage: String?,
    reviews: List<StoreReviewModel>,
    averageRating: Double,
    totalReviews: Int,
    isGuest: Boolean = false,
    onGuestSignInRequired: ((GuestPromptReason) -> Unit)? = null,
    onNavigateToListing: (StoreListing) -> Unit,
    onSubmitReview: (Int, String) -> Unit,
) {
    val context = LocalContext.current

    var selectedTab by rememberSaveable { mutableStateOf(StoreTab.PRODUCTS) }
    var showPostReview by remember { mutableStateOf(false) }

    LaunchedEffect(submitSuccess) {
        if (submitSuccess) showPostReview = false
    }

    Scaffold(
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = storeName.ifBlank { "Store" },
                            color = PrimaryColor,
                        )
                    },
                    actions = {
                        if (selectedTab == StoreTab.REVIEWS) {
                            // Show review button only for other users' stores
                            if (storeID != currentUserID) {
                                IconButton(onClick = {
                                    if (isGuest) {
                                        onGuestSignInRequired?.invoke(GuestPromptReason.REVIEW)
                                    } else {
                                        showPostReview = true
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (hasReviewed) Icons.Outlined.EditNote
                                        else Icons.Filled.Edit,
                                        contentDescription = if (hasReviewed) "Edit review"
                                        else "Write review",
                                        tint = PrimaryColor,
                                    )
                                }
                            }
                        } else {
                            // WhatsApp — guests see prompt, authenticated users open WhatsApp
                            IconButton(
                                onClick = {
                                    if (isGuest) {
                                        onGuestSignInRequired?.invoke(GuestPromptReason.CONTACT)
                                    } else {
                                        openWhatsApp(phoneNumber, storeName, context)
                                    }
                                },
                                // Guests always enabled (shows prompt)
                                // Authenticated users disabled until phone loaded
                                enabled = isGuest || !phoneNumber.isNullOrBlank(),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Phone,
                                    contentDescription = "Contact via WhatsApp",
                                    tint = if (isGuest || !phoneNumber.isNullOrBlank())
                                        Color(0xFF34C759) else Color.Gray,
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                )

                Column {
                    StoreTabBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                    )
                    HorizontalDivider()
                }
            }
        },
    ) { innerPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(top = 16.dp, bottom = 30.dp),
        ) {
            StoreProfileHeader(
                storeName = storeName,
                ownerName = ownerName,
                suburb = suburb,
                location = location,
                profileImageURL = profileImageURL,
            )

            when (selectedTab) {
                StoreTab.PRODUCTS -> ProductsTab(
                    isLoading = isLoading,
                    products = products,
                    onNavigateToListing = onNavigateToListing,
                )
                StoreTab.SERVICES -> StoreServicesTab(services = services)
                StoreTab.REVIEWS  -> StoreReviewsTab(
                    isLoading = isLoading,
                    reviews = reviews,
                    averageRating = averageRating,
                    totalReviews = totalReviews,
                )
            }
        }
    }

    // Post review sheet — only for authenticated users
    if (showPostReview) {
        PostReviewSheet(
            storeOwnerUID = ownerUID,
            hasReviewed = hasReviewed,
            isSubmitting = isSubmitting,
            errorMessage = reviewErrorMessage,
            submitSuccess = submitSuccess,
            onSubmit = onSubmitReview,
            onDismiss = { showPostReview = false },
        )
    }
}

// ── WhatsApp deep link ────────────────────────────────────────────────────────

private fun openWhatsApp(phoneNumber: String?, storeName: String, context: Context) {
    if (phoneNumber.isNullOrBlank()) return

    val name = storeName.ifBlank { "your store" }
    val message = "Hi! I came across $name on Super App ZW and I'm interested in your " +
            "products and services. Could you please share more details? " +
            "Looking forward to hearing from you!"

    val encoded = Uri.encode(message)
    val cleanedNumber = phoneNumber.trim().replace(" ", "")
    val url = "https://wa.me/$cleanedNumber?text=$encoded"

    val whatsAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        setPackage("com.whatsapp")
    }

    try {
        context.startActivity(whatsAppIntent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}

// ── Products tab ──────────────────────────────────────────────────────────────

@Composable
private fun ProductsTab(
    isLoading: Boolean,
    products: List<StoreListing>,
    onNavigateToListing: (StoreListing) -> Unit,
) {
    when {
        isLoading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = PrimaryColor)
            }
        }
        products.isEmpty() -> {
            EmptyStateView(icon = Icons.Outlined.Inbox, message = "No products listed yet")
        }
        else -> {
            val listingMap = remember(products) { products.associateBy { it.itemCode } }
            ListingsGridView(
                listings = products,
                onTap = { itemCode, _ -> listingMap[itemCode]?.let { onNavigateToListing(it) } },
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun StoreProfileViewPreview() {
    SuperAppZWTheme {
        StoreProfileContent(
            storeID = "preview_id",
            currentUserID = "other_id",
            storeName = "Super Store ZW",
            ownerName = "John Doe",
            suburb = "Harare Central",
            location = "Harare, Zimbabwe",
            profileImageURL = null,
            ownerUID = "preview_id",
            products = listOf(
                StoreListing(title = "Sample Product 1", description = "Description 1", price = 100.0, currency = "USD", itemCode = "P1", imageURL = null, viewCount = 10, ownerUserID = "preview_id"),
                StoreListing(title = "Sample Product 2", description = "Description 2", price = 50.0, currency = "USD", itemCode = "P2", imageURL = null, viewCount = 5, ownerUserID = "preview_id")
            ),
            services = listOf("Consulting", "Delivery", "Repair"),
            isLoading = false,
            phoneNumber = "+263771234567",
            hasReviewed = false,
            isSubmitting = false,
            submitSuccess = false,
            reviewErrorMessage = null,
            reviews = listOf(
                StoreReviewModel(reviewerName = "Alice", comment = "Great service!", rating = 5),
                StoreReviewModel(reviewerName = "Bob", comment = "Good products.", rating = 4)
            ),
            averageRating = 4.5,
            totalReviews = 2,
            onNavigateToListing = {},
            onSubmitReview = { _, _ -> }
        )
    }
}