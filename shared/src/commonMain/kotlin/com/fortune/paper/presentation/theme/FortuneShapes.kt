package com.fortune.paper.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

/**
 * MaterialTheme.shapes 매핑 — FortuneRadius 토큰 기준.
 * small=8, medium=16, large=20 (디자인 radius 스케일).
 */
val FortuneShapes = Shapes(
    extraSmall = RoundedCornerShape(FortuneRadius.sm),
    small = RoundedCornerShape(FortuneRadius.sm),
    medium = RoundedCornerShape(FortuneRadius.md),
    large = RoundedCornerShape(FortuneRadius.lg),
    extraLarge = RoundedCornerShape(FortuneRadius.lg),
)
