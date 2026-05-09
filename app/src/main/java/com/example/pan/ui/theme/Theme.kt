package com.example.pan.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PanLightColors = lightColorScheme(
    primary            = AuebBlue,
    onPrimary          = PanOnPrimary,
    primaryContainer   = AuebBluePale,
    onPrimaryContainer = AuebBlueDark,
    secondary          = AuebGold,
    onSecondary        = PanOnBackground,
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFF3E2700),
    background         = PanBackground,
    onBackground       = PanOnBackground,
    surface            = PanSurface,
    onSurface          = PanOnBackground,
    surfaceVariant     = AuebBluePale,
    onSurfaceVariant   = PanOnSurfaceVariant,
    error              = PanError,
    onError            = PanOnPrimary,
)

private val PanDarkColors = darkColorScheme(
    primary            = AuebBlueDarkTheme,
    onPrimary          = AuebBlueDark,
    primaryContainer   = AuebBlueMid,
    onPrimaryContainer = AuebBluePale,
    secondary          = AuebGold,
    onSecondary        = PanOnBackground,
    background         = PanBackgroundDark,
    onBackground       = Color(0xFFE6E1E5),
    surface            = PanSurfaceDark,
    onSurface          = Color(0xFFE6E1E5),
    surfaceVariant     = Color(0xFF2A2A3A),
    onSurfaceVariant   = Color(0xFFB8BCD8),
    error              = Color(0xFFCF6679),
    onError            = Color(0xFF690020),
)

@Composable
fun PanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PanDarkColors else PanLightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}