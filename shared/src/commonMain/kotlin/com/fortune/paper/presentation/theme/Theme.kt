package com.fortune.paper.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import fortunereport.shared.generated.resources.Res
import fortunereport.shared.generated.resources.notosans_kr_black
import fortunereport.shared.generated.resources.notosans_kr_bold
import fortunereport.shared.generated.resources.notosans_kr_extra_bold
import fortunereport.shared.generated.resources.notosans_kr_extra_light
import fortunereport.shared.generated.resources.notosans_kr_light
import fortunereport.shared.generated.resources.notosans_kr_medium
import fortunereport.shared.generated.resources.notosans_kr_regular
import fortunereport.shared.generated.resources.notosans_kr_semi_bold
import fortunereport.shared.generated.resources.notosans_kr_thin
import org.jetbrains.compose.resources.Font

private val FortuneColorScheme = lightColorScheme(
    primary = FortuneColors.blue500,
    onPrimary = FortuneColors.white,
    primaryContainer = FortuneColors.blue400,
    background = FortuneColors.bgPrimary,
    onBackground = FortuneColors.textPrimary,
    surface = FortuneColors.bgSurface,
    onSurface = FortuneColors.textPrimary,
    surfaceVariant = FortuneColors.cream300,
    onSurfaceVariant = FortuneColors.textTertiary,
    outline = FortuneColors.borderDefault,
    error = FortuneColors.error,
    onError = FortuneColors.white,
)

@Composable
private fun notoSansKr(): FontFamily = FontFamily(
    Font(Res.font.notosans_kr_thin, FontWeight.Thin),
    Font(Res.font.notosans_kr_extra_light, FontWeight.ExtraLight),
    Font(Res.font.notosans_kr_light, FontWeight.Light),
    Font(Res.font.notosans_kr_regular, FontWeight.Normal),
    Font(Res.font.notosans_kr_medium, FontWeight.Medium),
    Font(Res.font.notosans_kr_semi_bold, FontWeight.SemiBold),
    Font(Res.font.notosans_kr_bold, FontWeight.Bold),
    Font(Res.font.notosans_kr_extra_bold, FontWeight.ExtraBold),
    Font(Res.font.notosans_kr_black, FontWeight.Black),
)

@Composable
private fun fortuneTypography(family: FontFamily): Typography {
    val d = Typography()
    return Typography(
        displayLarge = d.displayLarge.copy(fontFamily = family),
        displayMedium = d.displayMedium.copy(fontFamily = family),
        displaySmall = d.displaySmall.copy(fontFamily = family),
        headlineLarge = d.headlineLarge.copy(fontFamily = family),
        headlineMedium = d.headlineMedium.copy(fontFamily = family),
        headlineSmall = d.headlineSmall.copy(fontFamily = family),
        titleLarge = d.titleLarge.copy(fontFamily = family),
        titleMedium = d.titleMedium.copy(fontFamily = family),
        titleSmall = d.titleSmall.copy(fontFamily = family),
        bodyLarge = d.bodyLarge.copy(fontFamily = family),
        bodyMedium = d.bodyMedium.copy(fontFamily = family),
        bodySmall = d.bodySmall.copy(fontFamily = family),
        labelLarge = d.labelLarge.copy(fontFamily = family),
        labelMedium = d.labelMedium.copy(fontFamily = family),
        labelSmall = d.labelSmall.copy(fontFamily = family),
    )
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FortuneColorScheme,
        typography = fortuneTypography(notoSansKr()),
        content = content
    )
}
