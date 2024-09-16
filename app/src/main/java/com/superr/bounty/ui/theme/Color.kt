package com.superr.bounty.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private const val TAG = "Superr.Theme.Color"

data class SuperrColorScheme(
    val White: Color = Color(0xFFFFFFFF),
    val Gray50: Color = Color(0xFFFAFAFA),
    val Gray100: Color = Color(0xFFF5F5F5),
    val Gray200: Color = Color(0xFFE5E5E5),
    val Gray300: Color = Color(0xFFD4D4D4),
    val Gray400: Color = Color(0xFFA3A3A3),
    val Gray500: Color = Color(0xFF737373),
    val Gray600: Color = Color(0xFF525252),
    val Gray700: Color = Color(0xFF404040),
    val Gray800: Color = Color(0xFF262626),
    val Gray900: Color = Color(0xFF171717),
    val Gray950: Color = Color(0xFF0A0A0A),
    val Black: Color = Color(0xFF000000)
)

val defaultColorScheme = SuperrColorScheme()

val lightSuperrColorScheme = defaultColorScheme
val darkSuperrColorScheme = defaultColorScheme

val materialDarkColorScheme = darkColorScheme(
    primary = defaultColorScheme.White,
    onPrimary = defaultColorScheme.Black,
    secondary = defaultColorScheme.Gray100,
    onSecondary = defaultColorScheme.Gray800,
    tertiary = defaultColorScheme.White,
    onTertiary = defaultColorScheme.Gray600,
    background = defaultColorScheme.Black,
    onBackground = defaultColorScheme.White,
    surface = defaultColorScheme.Black,
    onSurface = defaultColorScheme.White
)

val materialLightColorScheme = lightColorScheme(
    primary = defaultColorScheme.Black,
    onPrimary = defaultColorScheme.White,
    secondary = defaultColorScheme.Gray800,
    onSecondary = defaultColorScheme.Gray100,
    tertiary = defaultColorScheme.Gray600,
    onTertiary = defaultColorScheme.White,
    background = defaultColorScheme.White,
    onBackground = defaultColorScheme.Black,
    surface = defaultColorScheme.White,
    onSurface = defaultColorScheme.Black
)
