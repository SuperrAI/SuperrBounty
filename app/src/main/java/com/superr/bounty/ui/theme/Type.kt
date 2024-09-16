package com.superr.bounty.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.superr.bounty.R
import com.superr.bounty.utils.fsp

private const val TAG = "Superr.Theme.Type"

val GeistFontFamily = FontFamily(
    Font(R.font.geist_ultralight, FontWeight.ExtraLight),
    Font(R.font.geist_light, FontWeight.Light),
    Font(R.font.geist_thin, FontWeight.Thin),
    Font(R.font.geist_regular, FontWeight.Normal),
    Font(R.font.geist_medium, FontWeight.Medium),
    Font(R.font.geist_bold, FontWeight.Bold),
    Font(R.font.geist_semibold, FontWeight.SemiBold),
    Font(R.font.geist_ultrablack, FontWeight.ExtraBold),
)

val PlayfairDisplayFontFamily = FontFamily(
    /*
        Font(R.font.playfairdisplay_regular, FontWeight.ExtraLight),
        Font(R.font.playfairdisplay_regular, FontWeight.Light),
        Font(R.font.playfairdisplay_regular, FontWeight.Thin),
        Font(R.font.playfairdisplay_regular, FontWeight.Normal),
        Font(R.font.playfairdisplay_regular, FontWeight.Medium),
        Font(R.font.playfairdisplay_regular, FontWeight.Bold),
        Font(R.font.playfairdisplay_regular, FontWeight.SemiBold),
    */
    Font(R.font.playfairdisplay_extrabold, FontWeight.ExtraBold),
)

val RawNoteFontFamily = FontFamily(
    Font(R.font.atp_raw_note)
)

// Extension functions for TextStyle
fun TextStyle.withColor(color: Color): TextStyle = copy(color = color)
fun TextStyle.withFontSize(fontSize: TextUnit): TextStyle = copy(fontSize = fontSize)
fun TextStyle.withFontFamily(fontFamily: FontFamily): TextStyle = copy(fontFamily = fontFamily)
fun TextStyle.withFontWeight(fontWeight: FontWeight): TextStyle = copy(fontWeight = fontWeight)
fun TextStyle.withLetterSpacing(letterSpacing: TextUnit): TextStyle =
    copy(letterSpacing = letterSpacing)

fun TextStyle.withLineHeight(lineHeight: TextUnit): TextStyle = copy(lineHeight = lineHeight)
fun TextStyle.withTextAlign(textAlign: TextAlign): TextStyle = copy(textAlign = textAlign)

data class SuperrTypography(
    val labelSmall: TextStyle,
    val labelMedium: TextStyle,
    val labelLarge: TextStyle,
    val bodySmall: TextStyle,
    val bodyMedium: TextStyle,
    val bodyLarge: TextStyle,
    val titleSmall: TextStyle,
    val titleMedium: TextStyle,
    val titleLarge: TextStyle,
    val headlineMedium: TextStyle,
)

val Typography = SuperrTypography(
    labelSmall = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.fsp,
        lineHeight = 22.fsp,
    ),
    labelMedium = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.fsp,
        lineHeight = 26.fsp,
    ),
    labelLarge = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.fsp,
        lineHeight = 27.4.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.fsp,
        lineHeight = 24.fsp,
    ),
    bodyMedium = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.fsp,
        lineHeight = 24.8.fsp,
    ),
    bodyLarge = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.fsp,
        lineHeight = 26.fsp,
    ),
    titleSmall = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.fsp,
        lineHeight = 36.fsp,
    ),
    titleMedium = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.fsp,
        lineHeight = 43.84.fsp,
    ),
    titleLarge = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.fsp,
        lineHeight = 49.6.fsp,
        letterSpacing = (-0.02).fsp
    ),
    headlineMedium = TextStyle(
        fontFamily = GeistFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.fsp,
        lineHeight = 56.fsp,
        letterSpacing = (-0.02).fsp
    )
)

val materialTypography = Typography(
    labelSmall = Typography.labelSmall,
    labelMedium = Typography.labelMedium,
    labelLarge = Typography.labelLarge,
    bodySmall = Typography.bodySmall,
    bodyMedium = Typography.bodyMedium,
    bodyLarge = Typography.bodyLarge,
    titleSmall = Typography.titleSmall,
    titleMedium = Typography.titleMedium,
    titleLarge = Typography.titleLarge,
    headlineMedium = Typography.headlineMedium
)