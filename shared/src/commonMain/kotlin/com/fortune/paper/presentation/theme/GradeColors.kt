package com.fortune.paper.presentation.theme

import androidx.compose.ui.graphics.Color
import com.fortune.paper.domain.model.FortuneGrade

/** 날씨 등급 → 아이콘/글로우 색. */
fun FortuneGrade.color(): Color = when (this) {
    FortuneGrade.SUNNY -> FortuneColors.gradeSunny
    FortuneGrade.CLEAR -> FortuneColors.gradeClear
    FortuneGrade.CLOUDY -> FortuneColors.gradeCloudy
    FortuneGrade.RAINY -> FortuneColors.gradeRainy
    FortuneGrade.STORM -> FortuneColors.gradeStorm
}

/** 날씨 등급 → 흰 카드 위 키워드 텍스트 색(진한 변형). */
fun FortuneGrade.headlineColor(): Color = when (this) {
    FortuneGrade.SUNNY -> FortuneColors.gradeSunnyHeadline
    FortuneGrade.CLEAR -> FortuneColors.gradeClearHeadline
    FortuneGrade.CLOUDY -> FortuneColors.gradeCloudyHeadline
    FortuneGrade.RAINY -> FortuneColors.gradeRainyHeadline
    FortuneGrade.STORM -> FortuneColors.gradeStormHeadline
}
