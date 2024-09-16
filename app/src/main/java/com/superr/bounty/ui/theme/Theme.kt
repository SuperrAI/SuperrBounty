package com.superr.bounty.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private const val TAG = "Superr.Theme.Theme"

private val LocalSuperrColorScheme = staticCompositionLocalOf<SuperrColorScheme> {
    error("No SuperrColorScheme provided")
}

private val LocalSuperrTypography = staticCompositionLocalOf<SuperrTypography> {
    error("No SuperrTypography provided")
}

object SuperrTheme {
    val colorScheme: SuperrColorScheme
        @Composable
        get() = LocalSuperrColorScheme.current

    val typography: SuperrTypography
        @Composable
        get() = LocalSuperrTypography.current
}

@Composable
fun SuperrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkSuperrColorScheme else lightSuperrColorScheme
    val typography = Typography

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.Black.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalSuperrColorScheme provides colorScheme,
        LocalSuperrTypography provides typography
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) materialDarkColorScheme else materialLightColorScheme,
            typography = materialTypography
        ) {
            content()
        }
    }
}