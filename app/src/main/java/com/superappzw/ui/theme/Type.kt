package com.superappzw.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.superappzw.R

// Set of Material typography styles to start with
val FuturaFonts = mapOf(
    "Futura_Book_Regular_v1" to Font(R.font.futura_book, FontWeight.Normal, FontStyle.Normal),
    "Futura_Book_Italic_v1" to Font(R.font.futura_book_italic, FontWeight.Normal, FontStyle.Italic),
    "Futura_Bold_Regular_v1" to Font(R.font.futura_bold, FontWeight.Bold, FontStyle.Normal),
    "Futura_Bold_Italic_v1" to Font(R.font.futura_bold_italic, FontWeight.Bold, FontStyle.Italic),
    "Futura_Light_Regular_v1" to Font(R.font.futura_light, FontWeight.Light, FontStyle.Normal),
    "Futura_Light_Italic_v1" to Font(R.font.futura_light_italic, FontWeight.Light, FontStyle.Italic),
    "Futura_ExtraBold_Regular_v1" to Font(R.font.futura_heavy, FontWeight.ExtraBold, FontStyle.Normal),
    "Futura_ExtraBold_Italic_v1" to Font(R.font.futura_heavy_italic, FontWeight.ExtraBold, FontStyle.Italic),
    "Futura_Black_Regular_v1" to Font(R.font.futura_extra_black, FontWeight.Black, FontStyle.Normal),
    "Futura_Medium_Italic_v1" to Font(R.font.futura_medium_italic, FontWeight.Medium, FontStyle.Italic),
    "Futura_Medium_Regular_v1" to Font(R.font.futura_medium_bt, FontWeight.Medium, FontStyle.Normal),
    "Futura_MediumCondensed_Regular_v1" to Font(R.font.futura_medium_condensed_bt, FontWeight.Medium, FontStyle.Normal),
    "Futura_CondensedLight_Regular_v1" to Font(R.font.futura_condensed_light, FontWeight.Light, FontStyle.Normal)
)

val FuturaBookFamily = FontFamily(FuturaFonts["Futura_Book_Regular_v1"]!!)
val FuturaBoldFamily = FontFamily(FuturaFonts["Futura_Bold_Regular_v1"]!!)
val FuturaExtraBoldFamily = FontFamily(FuturaFonts["Futura_ExtraBold_Regular_v1"]!!)
val FuturaMediumFamily = FontFamily(FuturaFonts["Futura_Medium_Regular_v1"]!!)
val FuturaLightFamily = FontFamily(FuturaFonts["Futura_Light_Regular_v1"]!!)


val Typography = Typography(
    headlineSmall = TextStyle(
        fontFamily = FuturaMediumFamily,  // guarantees futura_bold
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FuturaExtraBoldFamily, // guarantees futura_heavy
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FuturaBookFamily,  // guarantees futura_book
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FuturaMediumFamily, // medium font
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FuturaLightFamily, // light font
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
