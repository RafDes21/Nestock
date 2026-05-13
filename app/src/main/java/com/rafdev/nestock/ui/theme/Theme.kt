package com.rafdev.nestock.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GreenDark,
    onPrimary = Surface,
    primaryContainer = GreenPale,
    onPrimaryContainer = GreenDark,
    secondary = GreenMid,
    onSecondary = Surface,
    secondaryContainer = GreenPale,
    onSecondaryContainer = GreenDark,
    tertiary = OrangeAlert,
    onTertiary = Surface,
    tertiaryContainer = OrangePale,
    onTertiaryContainer = OrangeAlert,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Background,
    onSurfaceVariant = TextMuted,
    outline = Border,
    error = OrangeAlert,
    onError = Surface
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = AppBg,
    primaryContainer = GreenDark,
    onPrimaryContainer = GreenPale,
    secondary = GreenMid,
    onSecondary = AppBg,
    background = BackgroundDark,
    onBackground = GreenPale,
    surface = SurfaceDark,
    onSurface = GreenPale,
    outline = GreenDark,
    error = OrangeAlert,
    onError = Surface
)

@Composable
fun NestockTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
