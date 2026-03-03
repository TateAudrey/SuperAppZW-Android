package com.superappzw.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superappzw.model.DailyLanguageModel
import com.superappzw.navigation.CustomNavBar
import com.superappzw.ui.components.buttons.PrimaryActionButton
import com.superappzw.ui.home.billboard.BillboardSectionView
import com.superappzw.ui.home.billboard.BillboardViewModel
import com.superappzw.ui.home.province.ProvinceDropDown
import com.superappzw.ui.home.province.ProvinceViewModel
import com.superappzw.ui.theme.PrimaryColor
import com.superappzw.ui.theme.SuperAppZWTheme

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    currentUserName: String? = null,
    currentUserPhotoUrl: String? = null,
    dailyLanguage: DailyLanguageModel? = null,
    onProfileTap: (() -> Unit)? = null,
    provinceViewModel: ProvinceViewModel = viewModel(),
    billboardViewModel: BillboardViewModel = viewModel(),
) {
    // ── Derived values (mirrors Swift computed properties) ────────────────────

    // First name only, fallback to "there" if not loaded yet (matches Swift)
    val firstName = remember(currentUserName) {
        val first = currentUserName
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() && it != "User" }
        first ?: "there"
    }

    // "Mangwanani, Tatenda" from language data, else "Hello, Tatenda"
    val dynamicTitle = remember(dailyLanguage, firstName) {
        if (dailyLanguage != null) {
            "${dailyLanguage.greeting}, $firstName"
        } else {
            "Hello, $firstName"
        }
    }

    // Tooltip only appears once language data is loaded
    val tooltipData = remember(dailyLanguage) {
        dailyLanguage?.let {
            NavTooltipData(
                title = "Language: ${it.languageID}",   // e.g. "Language: ChiShona"
                subtitle = it.summary,                  // full summary from Firestore
            )
        }
    }

    LaunchedEffect(Unit) {
        billboardViewModel.load()
    }

    SuperAppZWTheme {
        Column(modifier = modifier.fillMaxSize()) {

            // ── Nav bar
            CustomNavBar(
                title = dynamicTitle,
                profileImageURL = currentUserPhotoUrl,
                userName = firstName,
                tooltipData = tooltipData,
                onProfileTap = onProfileTap,
            )

            //Province Dropdown

            ProvinceDropDown(
                viewModel = provinceViewModel,
                modifier = Modifier.padding(top = 20.dp),
            )

            // ── Billboard
            BillboardSectionView(
                viewModel = billboardViewModel,
                onTap = { /* TODO: navigate to listing */ },
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}


// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Home – language loaded", showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
private fun HomeViewWithLanguagePreview() {
    SuperAppZWTheme {
        HomeView(
            currentUserName = "Tatenda Moyo",
            currentUserPhotoUrl = null,
            dailyLanguage = DailyLanguageModel(
                languageID = "ChiShona",
                greeting = "Mangwanani",
                summary = "ChiShona is spoken by over 10 million people across Zimbabwe.",
            ),
        )
    }
}

