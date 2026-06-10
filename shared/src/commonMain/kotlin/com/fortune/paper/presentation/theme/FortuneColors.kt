package com.fortune.paper.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * design/FortunePaper_Design/design-system/colors_and_type.css 의 토큰을 옮긴 값.
 * MaterialTheme 슬롯에 매핑되지 않는 추가 토큰(크림 배경 단계, 날씨 등급 색)을 여기서 노출한다.
 */
object FortuneColors {
    // Neutrals
    val white = Color(0xFFFFFFFF)
    val cream100 = Color(0xFFFBFAF6)
    val cream200 = Color(0xFFF6F5EE)
    val cream300 = Color(0xFFEEEEE6)

    val gray200 = Color(0xFFE5E7EB)
    val gray300 = Color(0xFFD1D5DB)
    val gray400 = Color(0xFF9CA3AF)
    val gray500 = Color(0xFF6B7280)
    val gray900 = Color(0xFF111827)

    // Brand
    val blue400 = Color(0xFF60A5FA)
    val blue500 = Color(0xFF3B82F6)
    val blue600 = Color(0xFF2563EB)

    // Semantic
    val bgPrimary = cream200
    val bgSurface = white
    val textPrimary = gray900
    val textSecondary = gray400
    val textTertiary = gray500
    val borderDefault = gray200
    val error = Color(0xFFDC2626)

    // Grade (날씨 등급) — color: 아이콘/글로우 색
    val gradeSunny = Color(0xFFFFD700)
    val gradeClear = Color(0xFF87CEEB)
    val gradeCloudy = Color(0xFFB0BEC5)
    val gradeRainy = Color(0xFF5C8AC8)
    val gradeStorm = Color(0xFF546E7A)

    // Grade headline — 흰 카드 위 등급 키워드 텍스트용 (color의 진한 변형, components.jsx GRADES 기준)
    val gradeSunnyHeadline = Color(0xFFC99000)
    val gradeClearHeadline = Color(0xFF2E7DA8)
    val gradeCloudyHeadline = Color(0xFF607883)
    val gradeRainyHeadline = Color(0xFF3F6DAC)
    val gradeStormHeadline = Color(0xFF37474F)
}
