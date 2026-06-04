package com.fortune.paper.presentation.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * design-system/colors_and_type.css 의 TYPE 스케일(sp ≈ px).
 * 기존 MaterialTheme.typography(폰트 패밀리 보유) 슬롯에 `.copy(fontSize = ...)` 로 적용해
 * Noto Sans KR 패밀리를 유지하면서 디자인 스케일을 입힌다.
 */
object FortuneType {
    // size
    val headingXl = 32.sp   // 날씨 등급 HERO
    val headingLg = 24.sp   // 화면 제목
    val headingMd = 20.sp   // 섹션 제목
    val gradeName = 30.sp   // revealed 등급 키워드
    val bodyLg = 16.sp      // 한 줄 요약
    val bodyMd = 14.sp      // 기본 본문
    val bodySm = 13.sp      // 보조 본문
    val caption = 11.sp     // 날짜·메타

    // line-height
    val lhTight = 1.2f
    val lhSnug = 1.35f
    val lhNormal = 1.5f
    val lhLoose = 1.7f

    // weight
    val regular = FontWeight.Normal
    val medium = FontWeight.Medium
    val semibold = FontWeight.SemiBold
    val bold = FontWeight.Bold
    val extraBold = FontWeight.ExtraBold
}
