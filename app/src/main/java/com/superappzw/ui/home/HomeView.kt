package com.superappzw.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.model.DailyLanguageModel
import com.superappzw.navigation.CustomNavBar
import com.superappzw.ui.categories.CategoryItem
import com.superappzw.ui.categories.PopularCategoriesSection
import com.superappzw.ui.home.billboard.BillboardSectionView
import com.superappzw.ui.home.billboard.BillboardViewModel
import com.superappzw.ui.home.province.ProvinceDropDown
import com.superappzw.ui.home.province.ProvinceViewModel
import com.superappzw.ui.lisitngs.ListingsSectionView
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    currentUserName: String? = null,
    currentUserPhotoUrl: String? = null,
    dailyLanguage: DailyLanguageModel? = null,
    onProfileTap: (() -> Unit)? = null,
    onCategorySelect: ((CategoryItem) -> Unit)? = null,
    onListingTap: ((itemCode: String, ownerUserID: String) -> Unit)? = null,
    provinceViewModel: ProvinceViewModel = viewModel(),
    billboardViewModel: BillboardViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    onStoreTap: ((userID: String) -> Unit)? = null,
) {
    // ── Derived values ────────────────────────────────────────────────────────

    val firstName = remember(currentUserName) {
        val first = currentUserName
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() && it != "User" }
        first ?: "Stranger"
    }

    val dynamicTitle = remember(dailyLanguage, firstName) {
        if (dailyLanguage != null) "${dailyLanguage.greeting}, $firstName"
        else "Hello, $firstName"
    }

    val tooltipData = remember(dailyLanguage) {
        dailyLanguage?.let {
            NavTooltipData(
                title = "Language: ${it.languageID}",
                subtitle = it.summary,
            )
        }
    }

    // ── Observe selected province ─────────────────────────────────────────────

    val selectedProvince by provinceViewModel.selectedProvince.collectAsState()

    // ── Refresh listings on resume (keeps view counts in sync) ───────────────

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            billboardViewModel.load()
            homeViewModel.refreshListings(province = selectedProvince)
        }
    }

    // ── Re-fetch when province changes ────────────────────────────────────────

    LaunchedEffect(selectedProvince) {
        homeViewModel.refreshListings(province = selectedProvince)
    }

    SuperAppZWTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {

            // ── Nav bar ───────────────────────────────────────────────────────
            CustomNavBar(
                title = dynamicTitle,
                profileImageURL = currentUserPhotoUrl,
                userName = firstName,
                tooltipData = tooltipData,
                onProfileTap = onProfileTap,
            )

            // ── Province dropdown ─────────────────────────────────────────────
            ProvinceDropDown(
                viewModel = provinceViewModel,
                modifier = Modifier.padding(top = 20.dp),
            )

            // ── Billboard carousel ────────────────────────────────────────────
            BillboardSectionView(
                viewModel = billboardViewModel,
                onStoreTap = { userID -> onStoreTap?.invoke(userID) },
                modifier = Modifier.padding(top = 16.dp),
            )

            // ── Browse Categories ─────────────────────────────────────────────
            Text(
                text = "Browse Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 4.dp),
            )

            PopularCategoriesSection(
                categories = CategoryItem.all,
                onSelect = { category -> onCategorySelect?.invoke(category) },
            )

            // ── Featured Listings ─────────────────────────────────────────────
            Text(
                text = "Featured Listings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp),
            )

            ListingsSectionView(
                viewModel = homeViewModel,
                onTap = { itemCode, ownerUserID ->
                    onListingTap?.invoke(itemCode, ownerUserID)
                },
                onRefresh = {
                    homeViewModel.refreshListings(province = selectedProvince)
                },
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Home – language loaded", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun HomeViewWithLanguagePreview() {
    SuperAppZWTheme {
        HomeView(
            currentUserName = "Tatenda Charks",
            currentUserPhotoUrl = null,
            dailyLanguage = DailyLanguageModel(
                languageID = "ChiShona",
                greeting = "Mangwanani",
                summary = "ChiShona is spoken by over 10 million people across Zimbabwe.",
            ),
        )
    }
}

@Preview(name = "Home – loading", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun HomeViewLoadingPreview() {
    SuperAppZWTheme {
        HomeView(
            currentUserName = "Tatenda Charks",
            dailyLanguage = null,
        )
    }
}

@Preview(name = "Home – no user", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun HomeViewNoUserPreview() {
    SuperAppZWTheme {
        HomeView()
    }
}

