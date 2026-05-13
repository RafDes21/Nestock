package com.rafdev.nestock.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Placeholder families — para usar Fraunces/DM Sans reales, agregá los .ttf en res/font/
val FrauncesFamily = FontFamily.Serif
val DMSansFamily   = FontFamily.SansSerif

val Typography = Typography(
    displayLarge  = TextStyle(fontFamily = FrauncesFamily, fontWeight = FontWeight.Bold,     fontSize = 36.sp, lineHeight = 44.sp),
    displayMedium = TextStyle(fontFamily = FrauncesFamily, fontWeight = FontWeight.Bold,     fontSize = 28.sp, lineHeight = 36.sp),
    headlineLarge = TextStyle(fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    headlineMedium= TextStyle(fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp),
    titleLarge    = TextStyle(fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleMedium   = TextStyle(fontFamily = DMSansFamily,   fontWeight = FontWeight.Medium,   fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall    = TextStyle(fontFamily = DMSansFamily,   fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge     = TextStyle(fontFamily = DMSansFamily,   fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium    = TextStyle(fontFamily = DMSansFamily,   fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall     = TextStyle(fontFamily = DMSansFamily,   fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge    = TextStyle(fontFamily = DMSansFamily,   fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium   = TextStyle(fontFamily = DMSansFamily,   fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall    = TextStyle(fontFamily = DMSansFamily,   fontWeight = FontWeight.Medium,   fontSize = 10.sp, lineHeight = 14.sp),
)
