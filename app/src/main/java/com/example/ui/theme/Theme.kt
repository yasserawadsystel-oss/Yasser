package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF081938),
    primaryContainer = SystelBlueCard,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = SystelAccentRed,
    onSecondary = Color.White,
    tertiary = SystelAccentCyan,
    background = SystelBgDark,
    surface = SystelSurfaceDark,
    onBackground = SystelTextPrimaryDark,
    onSurface = SystelTextPrimaryDark,
    surfaceVariant = SystelCardDark,
    onSurfaceVariant = SystelTextSecondaryDark,
    outline = SystelBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = SystelNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = SystelBlueLight,
    onPrimaryContainer = SystelNavyPrimary,
    secondary = SystelAccentRed,
    onSecondary = Color.White,
    tertiary = SystelAccentCyan,
    background = SystelBgLight,
    surface = SystelSurfaceLight,
    onBackground = SystelTextPrimary,
    onSurface = SystelTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SystelTextSecondary,
    outline = SystelBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent Systel Telecom branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
