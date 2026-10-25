package com.giannatech.multigroupmessage.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Flip7DarkColorScheme = darkColorScheme(
    primary = Flip7NeonGreen,
    onPrimary = Flip7Background,
    primaryContainer = Flip7SurfaceVariant,
    onPrimaryContainer = Flip7NeonGreen,

    secondary = Flip7ElectricCyan,
    onSecondary = Flip7Background,
    secondaryContainer = Flip7SurfaceVariant,
    onSecondaryContainer = Flip7ElectricCyan,

    tertiary = Flip7CyberGold,
    onTertiary = Flip7Background,

    background = Flip7Background,
    onBackground = Flip7TextPrimary,

    surface = Flip7Surface,
    onSurface = Flip7TextPrimary,
    surfaceVariant = Flip7SurfaceVariant,
    onSurfaceVariant = Flip7TextSecondary,

    error = Flip7VividCrimson,
    onError = Flip7TextPrimary,

    outline = Flip7Border,
    outlineVariant = Flip7Border
)

@Composable
fun MultiGroupMessageTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Flip7Background.toArgb()
            window.navigationBarColor = Flip7Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = Flip7DarkColorScheme,
        typography = Typography,
        content = content
    )
}
