package com.fortune.paper.presentation.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortune.paper.presentation.onboarding.OnboardingState
import com.fortune.paper.presentation.onboarding.components.Eyebrow
import com.fortune.paper.presentation.onboarding.components.StepSubtitle
import com.fortune.paper.presentation.onboarding.components.StepTitle
import com.fortune.paper.presentation.theme.FortuneColors

private data class Branch(val name: String, val range: String)

private val BRANCHES = listOf(
    Branch("자", "23–01시"), Branch("축", "01–03시"), Branch("인", "03–05시"), Branch("묘", "05–07시"),
    Branch("진", "07–09시"), Branch("사", "09–11시"), Branch("오", "11–13시"), Branch("미", "13–15시"),
    Branch("신", "15–17시"), Branch("유", "17–19시"), Branch("술", "19–21시"), Branch("해", "21–23시"),
)

@Composable
fun TimeStep(
    state: OnboardingState,
    onSelect: (String?) -> Unit,
) {
    Column {
        Spacer(Modifier.height(12.dp))
        Eyebrow("4 / 4 — 태어난 시각 (선택)")
        Spacer(Modifier.height(12.dp))
        StepTitle("몇 시에 태어났는지\n알고 계신가요?")
        Spacer(Modifier.height(10.dp))
        StepSubtitle("모르셔도 괜찮아요. 더 정확한 풀이가 필요할 때만 선택해 주세요.")
        Spacer(Modifier.height(20.dp))

        BRANCHES.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { branch ->
                    val active = state.birthTime == branch.name
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(
                                if (active) FortuneColors.blue500 else FortuneColors.bgSurface,
                                RoundedCornerShape(12.dp),
                            )
                            .clickable { onSelect(branch.name) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${branch.name}시",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) FortuneColors.white else FortuneColors.textPrimary,
                            )
                            Text(
                                branch.range,
                                fontSize = 9.sp,
                                color = if (active) FortuneColors.white.copy(alpha = 0.8f) else FortuneColors.textTertiary,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        val unknownActive = state.birthTime == null
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { onSelect(null) }
                .let { m ->
                    if (unknownActive) {
                        m.background(FortuneColors.blue500.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    } else m
                },
            contentAlignment = Alignment.Center,
        ) {
            // 점선 테두리 대신 단색 테두리 카드로 표현
            Text(
                "잘 모르겠어요",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (unknownActive) FortuneColors.blue500 else FortuneColors.textTertiary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
