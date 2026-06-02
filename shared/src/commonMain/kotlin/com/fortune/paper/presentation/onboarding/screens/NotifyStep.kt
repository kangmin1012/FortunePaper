package com.fortune.paper.presentation.onboarding.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortune.paper.presentation.onboarding.components.Eyebrow
import com.fortune.paper.presentation.onboarding.components.StepSubtitle
import com.fortune.paper.presentation.onboarding.components.StepTitle
import com.fortune.paper.presentation.theme.FortuneColors

private data class NotifyPreset(val time: String, val label: String)

private val PRESETS = listOf(
    NotifyPreset("06:30", "이른 아침"),
    NotifyPreset("07:30", "출근 전"),
    NotifyPreset("08:30", "아침 시간"),
    NotifyPreset("09:30", "느긋한 아침"),
)

@Composable
fun NotifyStep(
    selectedTime: String,
    onSelect: (String) -> Unit,
) {
    Column {
        Spacer(Modifier.height(12.dp))
        Eyebrow("마지막 — 알림")
        Spacer(Modifier.height(12.dp))
        StepTitle("리포트를 언제\n받아 보시겠어요?")
        Spacer(Modifier.height(10.dp))
        StepSubtitle("설정한 시간에 매일 한 번, 오늘의 한 줄 요약을 알려 드립니다.")
        Spacer(Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PRESETS.forEach { preset ->
                val active = selectedTime == preset.time
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(preset.time) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FortuneColors.bgSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = if (active) BorderStroke(2.dp, FortuneColors.blue500) else null,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (active) FortuneColors.blue500 else FortuneColors.cream300,
                                    CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("🔔", fontSize = 18.sp)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(
                                "오전 ${preset.time}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FortuneColors.textPrimary,
                            )
                            Text(
                                preset.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = FortuneColors.textTertiary,
                            )
                        }
                        if (active) {
                            Text("✓", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FortuneColors.blue500)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            "시간은 설정에서 언제든지 바꿀 수 있습니다.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = FortuneColors.textTertiary,
            textAlign = TextAlign.Center,
        )
    }
}
