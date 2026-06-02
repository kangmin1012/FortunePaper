package com.fortune.paper.presentation.onboarding.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.presentation.onboarding.components.CircleBadge
import com.fortune.paper.presentation.onboarding.components.Eyebrow
import com.fortune.paper.presentation.onboarding.components.StepSubtitle
import com.fortune.paper.presentation.onboarding.components.StepTitle
import com.fortune.paper.presentation.theme.FortuneColors

@Composable
fun GenderStep(
    selected: Gender?,
    onSelect: (Gender) -> Unit,
) {
    Column {
        Spacer(Modifier.height(12.dp))
        Eyebrow("3 / 4 — 성별")
        Spacer(Modifier.height(12.dp))
        StepTitle("성별을 알려 주세요")
        Spacer(Modifier.height(10.dp))
        StepSubtitle("사주의 음양 균형을 풀이하는 데 사용됩니다.")
        Spacer(Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GenderCard(
                modifier = Modifier.weight(1f),
                label = "여성",
                mark = "음",
                sub = "여성 사주",
                accent = Color(0xFFE8B4D2),
                active = selected == Gender.FEMALE,
                onClick = { onSelect(Gender.FEMALE) },
            )
            GenderCard(
                modifier = Modifier.weight(1f),
                label = "남성",
                mark = "양",
                sub = "남성 사주",
                accent = Color(0xFFFFD27A),
                active = selected == Gender.MALE,
                onClick = { onSelect(Gender.MALE) },
            )
        }

        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = FortuneColors.cream300),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Text(
                "사주에서는 음(여)·양(남)의 흐름이 다르게 풀이됩니다.",
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodySmall,
                color = FortuneColors.textTertiary,
            )
        }
    }
}

@Composable
private fun GenderCard(
    modifier: Modifier,
    label: String,
    mark: String,
    sub: String,
    accent: Color,
    active: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FortuneColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (active) 4.dp else 1.dp),
        border = if (active) BorderStroke(2.dp, FortuneColors.blue500) else null,
    ) {
        Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 20.dp)) {
            CircleBadge(text = mark, background = accent)
            Spacer(Modifier.height(18.dp))
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FortuneColors.textPrimary)
            Spacer(Modifier.height(4.dp))
            Text(sub, style = MaterialTheme.typography.bodySmall, color = FortuneColors.textTertiary)
        }
    }
}
