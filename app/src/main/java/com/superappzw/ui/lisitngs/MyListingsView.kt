package com.superappzw.ui.lisitngs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.superappzw.ui.components.utils.AppAlert
import com.superappzw.ui.components.utils.LoadingView
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsView(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MyListingsViewModel = viewModel(),
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val alertType by viewModel.alertType.collectAsState()
    val selectedListing by viewModel.selectedListing.collectAsState()
    val navigateToProfile by viewModel.navigateToProfile.collectAsState()

    // Load on first composition — mirrors .task { await viewModel.loadUserData() }
    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    // Navigate to listing detail when a listing is selected
    LaunchedEffect(selectedListing) {
        selectedListing?.let { listing ->
            navController.navigate("listingDetail/${listing.itemCode}/${listing.ownerUserID}")
            viewModel.setSelectedListing(null)
        }
    }

    // Navigate to profile when eligibility check triggers it
    LaunchedEffect(navigateToProfile) {
        if (navigateToProfile) {
            navController.navigate("profileDetail")
            viewModel.setNavigateToProfile(false)
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Listings",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F7)),
            ) {

                // ── Tab bar ───────────────────────────────────────────────────
                Column(modifier = Modifier.background(Color.White)) {
                    MyListingsTabBar(
                        selectedTab = selectedTab,
                        onTabSelected = viewModel::onTabSelected,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE0E0E0))
                }

                // ── Tab content ───────────────────────────────────────────────
                when (selectedTab) {
                    ListingTab.PRODUCTS -> {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            ProductsTabView(viewModel = viewModel)
                        }
                    }
                    ListingTab.SERVICES -> {
                        ServicesTabView(viewModel = viewModel)
                    }
                    ListingTab.REVIEWS -> {
                        ReviewTabView(viewModel = viewModel)
                    }
                    ListingTab.POST -> {
                        PostFormView(
                            selectedCategory   = viewModel.selectedCategory.collectAsState().value,
                            onCategorySelected = viewModel::onCategorySelected,
                            selectedCurrency   = viewModel.selectedCurrency.collectAsState().value,
                            onCurrencySelected = viewModel::onCurrencySelected,
                            selectedLocation   = viewModel.selectedLocation.collectAsState().value,
                            onLocationSelected = viewModel::onLocationSelected,
                            productName        = viewModel.productName.collectAsState().value,
                            onProductNameChange = viewModel::onProductNameChange,
                            priceText          = viewModel.priceText.collectAsState().value,
                            onPriceTextChange  = viewModel::onPriceTextChange,
                            isNegotiable       = viewModel.isNegotiable.collectAsState().value,
                            onNegotiableChange = viewModel::onNegotiableChange,
                            description        = viewModel.description.collectAsState().value,
                            onDescriptionChange = viewModel::onDescriptionChange,
                            selectedImage      = viewModel.selectedImage.collectAsState().value,
                            onImageSelected    = viewModel::onImageSelected,
                            onPublish          = viewModel::publishListing,
                        )
                    }
                }
            }

            // ── Loading overlay ───────────────────────────────────────────────
            if (isLoading) {
                LoadingView()
            }
        }
    }

    // ── Alert ─────────────────────────────────────────────────────────────────
    AppAlert(
        alertType = alertType,
        onDismiss = viewModel::dismissAlert,
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyListingsViewPreview() {
    SuperAppZWTheme {
        MyListingsView(navController = rememberNavController())
    }
}