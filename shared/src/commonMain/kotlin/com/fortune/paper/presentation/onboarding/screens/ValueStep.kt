package com.fortune.paper.presentation.onboarding.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortune.paper.presentation.onboarding.components.Eyebrow
import com.fortune.paper.presentation.theme.FortuneColors

private data class ValueItem(val emoji: String, val title: String, val body: String)

@Composable
fun ValueStep() {
    val items = listOf(
        ValueItem("☀️", "한 장의 리포트", "스크롤 없이 오늘의 운세 흐름을 한 화면에 담아 드립니다."),
        ValueItem("🌤️", "사주 기반", "생년월일과 성별을 토대로 매일 새로운 등급을 계산합니다."),
        ValueItem("☁️", "매일 아침", "원하는 시간에 알림으로 오늘의 한 줄 요약을 전해 드립니다."),
    )

    Column {
        Spacer(Modifier.height(8.dp))
        Eyebrow("포츈페이퍼란")
        Spacer(Modifier.height(12.dp))
        Text(
            "매일 한 장,\n오늘만큼의 위로.",
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            color = FortuneColors.textPrimary,
        )
        Spacer(Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FortuneColors.bgSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(item.emoji, fontSize = 40.sp)
                        Column(Modifier.weight(1f)) {
                            Text(
                                item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = FortuneColors.textPrimary,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                item.body,
                                style = MaterialTheme.typography.bodySmall,
                                color = FortuneColors.textTertiary,
                            )
                        }
                    }
                }
            }
        }
    }
}
