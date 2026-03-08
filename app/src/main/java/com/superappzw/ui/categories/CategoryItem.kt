package com.superappzw.ui.categories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MicExternalOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

data class CategoryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: ImageVector,
) {
    companion object {
        val all: List<CategoryItem> = listOf(
            CategoryItem(name = "Food & Beverages",                  icon = Icons.Filled.Restaurant),
            CategoryItem(name = "Electronics & Technology",          icon = Icons.Filled.Tv),
            CategoryItem(name = "Fashion & Accessories",             icon = Icons.Filled.CardGiftcard),
            CategoryItem(name = "Home, Furniture & Living",          icon = Icons.Filled.Bed),
            CategoryItem(name = "Beauty, Cosmetics & Personal Care", icon = Icons.Filled.Face),
            CategoryItem(name = "Agriculture & Farming",             icon = Icons.Filled.Eco),
            CategoryItem(name = "Health",                            icon = Icons.Filled.LocalHospital),
            CategoryItem(name = "Home Services",                     icon = Icons.Filled.Home),
            CategoryItem(name = "Professional Services",             icon = Icons.Filled.Build),
            CategoryItem(name = "Jobs, Gigs & Freelancers",          icon = Icons.Filled.Work),
            CategoryItem(name = "Property & Real Estate",            icon = Icons.Filled.Apartment),
            CategoryItem(name = "Automotive & Transport",            icon = Icons.Filled.DirectionsCar),
            CategoryItem(name = "Education & Training",              icon = Icons.Filled.School),
            CategoryItem(name = "Finance & Insurance",               icon = Icons.Filled.AttachMoney),
            CategoryItem(name = "Travel & Logistics",                icon = Icons.Filled.AirplanemodeActive),
            CategoryItem(name = "Events, Media & Entertainment",     icon = Icons.Filled.MicExternalOn),
            CategoryItem(name = "Other",                             icon = Icons.Filled.MoreHoriz),
        )
    }
}