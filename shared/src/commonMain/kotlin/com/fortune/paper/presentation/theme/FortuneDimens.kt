package com.fortune.paper.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * design-system/colors_and_type.css 의 SPACING / RADIUS 토큰.
 * 값 하드코딩 대신 이 토큰을 사용한다 (ui.md 규칙).
 */
object FortuneSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 48.dp
}

object FortuneRadius {
    val sm = 8.dp
    val md = 16.dp
    val lg = 20.dp
    val full = 999.dp
}
