package com.superappzw.ui.account.packages

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.superappzw.R
import com.superappzw.ui.theme.PrimaryColor

// ── Package appearance model ──────────────────────────────────────────────────

data class PackageAppearance(
    val color: Color,
    @DrawableRes val iconRes: Int,
)

// ── Package appearance resolver ───────────────────────────────────────────────

object PackageAppearanceResolver {

    fun resolve(packageName: String): PackageAppearance {
        return when {
            packageName.contains("standard", ignoreCase = true) ->
                PackageAppearance(
                    color = PrimaryColor,
                    iconRes = R.drawable.icon_standard,
                )
            packageName.contains("alpha", ignoreCase = true) ->
                PackageAppearance(
                    color = Color(0xFF6B1F81),
                    iconRes = R.drawable.icon_alpha,
                )
            packageName.contains("bravo", ignoreCase = true) ->
                PackageAppearance(
                    color = Color(0xFF49950E),
                    iconRes = R.drawable.icon_bravo,
                )
            packageName.contains("charlie", ignoreCase = true) ->
                PackageAppearance(
                    color = Color(0xFFCF0000),
                    iconRes = R.drawable.icon_charlie,
                )
            packageName.contains("delta", ignoreCase = true) ->
                PackageAppearance(
                    color = Color(0xFFD5971B),
                    iconRes = R.drawable.icon_delta,
                )
            else ->
                PackageAppearance(
                    color = PrimaryColor,
                    iconRes = R.drawable.icon_standard,
                )
        }
    }
}