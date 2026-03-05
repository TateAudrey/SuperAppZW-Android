package com.superappzw.ui.store

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.superappzw.model.StoreReviewModel
import com.superappzw.ui.components.utils.EmptyStateView
import com.superappzw.ui.lisitngs.ListingsGridView
import com.superappzw.ui.reviews.PostReviewSheet
import com.superappzw.ui.reviews.ReviewViewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreProfileView(
    storeID: String,
    onNavigateToListing: (StoreListing) -> Unit = {},
    storeProfileViewModel: StoreProfileViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
) {
    val currentUserID = try {
        FirebaseAuth.getInstance().currentUser?.uid
    } catch (e: Exception) {
        null
    }

    val storeName by storeProfileViewModel.storeName.collectAsState()
    val ownerName by storeProfileViewModel.ownerName.collectAsState()
    val suburb by storeProfileViewModel.suburb.collectAsState()
    val location by storeProfileViewModel.location.collectAsState()
    val profileImageURL by storeProfileViewModel.profileImageURL.collectAsState()
    val ownerUID by storeProfileViewModel.ownerUID.collectAsState()
    val products by storeProfileViewModel.products.collectAsState()
    val services by storeProfileViewModel.services.collectAsState()
    val isLoading by storeProfileViewModel.isLoading.collectAsState()

    val reviews by reviewViewModel.reviews.collectAsState()
    val averageRating by reviewViewModel.averageRating.collectAsState()
    val totalReviews by reviewViewModel.totalReviews.collectAsState()
    val hasReviewed by reviewViewModel.hasReviewed.collectAsState()
    val isSubmitting by reviewViewModel.isSubmitting.collectAsState()
    val submitSuccess by reviewViewModel.submitSuccess.collectAsState()
    val reviewErrorMessage by reviewViewModel.errorMessage.collectAsState()

    StoreProfileView(
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
        reviews = reviews,
        averageRating = averageRating,
        totalReviews = totalReviews,
        hasReviewed = hasReviewed,
        isSubmitting = isSubmitting,
        submitSuccess = submitSuccess,
        reviewErrorMessage = reviewErrorMessage,
        onNavigateToListing = onNavigateToListing,
        onLoadData = {
            storeProfileViewModel.load(storeID)
            reviewViewModel.load(storeID)
        },
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
fun StoreProfileView(
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
    reviews: List<StoreReviewModel>,
    averageRating: Double,
    totalReviews: Int,
    hasReviewed: Boolean,
    isSubmitting: Boolean,
    submitSuccess: Boolean,
    reviewErrorMessage: String?,
    onNavigateToListing: (StoreListing) -> Unit = {},
    onLoadData: () -> Unit = {},
    onSubmitReview: (Int, String) -> Unit = { _, _ -> },
) {
    var selectedTab by rememberSaveable { mutableStateOf(StoreTab.PRODUCTS) }
    var showPostReview by remember { mutableStateOf(false) }

    // Load both ViewModels on first composition — mirrors Swift's .task { }
    LaunchedEffect(storeID) {
        onLoadData()
    }

    // Auto-dismiss PostReviewSheet on successful submission
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
                            // Hide pencil if the current user owns this store
                            if (storeID != currentUserID) {
                                IconButton(onClick = { showPostReview = true }) {
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
                            // Phone button — no-op for now
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Filled.Phone,
                                    contentDescription = "Contact",
                                    tint = Color(0xFF34C759), // iOS .green equivalent
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                    ),
                )

                // ── Tab bar pinned below the top app bar ──────────────────────
                Column {
                    StoreTabBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        modifier = Modifier.padding(vertical = 8.dp),
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
            // ── Store profile header ───────────────────────────────────────────
            StoreProfileHeader(
                storeName = storeName,
                ownerName = ownerName,
                suburb = suburb,
                location = location,
                profileImageURL = profileImageURL,
            )

            // ── Tab content ───────────────────────────────────────────────────
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
                    totalReviews = totalReviews
                )
            }
        }
    }

    // ── Post review sheet ─────────────────────────────────────────────────────
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = PrimaryColor,
                )
            }
        }
        products.isEmpty() -> {
            EmptyStateView(
                icon = Icons.Filled.Phone, // placeholder — swap for tray/inbox icon
                message = "No products listed yet",
            )
        }
        else -> {
            ListingsGridView(
                listings = products,
                onTap = { onNavigateToListing(it) },
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun StoreProfileViewPreview() {
    SuperAppZWTheme {
        StoreProfileView(
            storeID = "UT0mHxc1IJcuRsibi3srlMbISZI2",
            currentUserID = null,
            storeName = "Sample Store",
            ownerName = "John Doe",
            suburb = "Harare",
            location = "Main St",
            profileImageURL = null,
            ownerUID = "owner123",
            products = emptyList(),
            services = emptyList(),
            isLoading = false,
            reviews = emptyList(),
            averageRating = 4.5,
            totalReviews = 10,
            hasReviewed = false,
            isSubmitting = false,
            submitSuccess = false,
            reviewErrorMessage = null
        )
    }
}