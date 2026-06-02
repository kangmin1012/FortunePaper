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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fortune.paper.presentation.onboarding.OnboardingState
import com.fortune.paper.presentation.onboarding.components.Eyebrow
import com.fortune.paper.presentation.onboarding.components.StepSubtitle
import com.fortune.paper.presentation.onboarding.components.StepTitle
import com.fortune.paper.presentation.onboarding.components.WheelPicker
import com.fortune.paper.presentation.theme.FortuneColors

private val YEARS = (1950..2010).toList()
private val MONTHS = (1..12).toList()
private val DAYS = (1..31).toList()

@Composable
fun BirthStep(
    state: OnboardingState,
    onPick: (year: Int, month: Int, day: Int) -> Unit,
) {
    Column {
        Spacer(Modifier.height(12.dp))
        Eyebrow("2 / 4 — 생년월일")
        Spacer(Modifier.height(12.dp))
        StepTitle("언제 태어나셨나요?")
        Spacer(Modifier.height(10.dp))
        StepSubtitle("사주 계산에 사용되며 안전하게 보관됩니다.")
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = FortuneColors.bgSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                WheelPicker(
                    items = YEARS,
                    selectedIndex = YEARS.indexOf(state.birthYear).coerceAtLeast(0),
                    onSelect = { i -> onPick(YEARS[i], state.birthMonth, state.birthDay) },
                    suffix = "년",
                    modifier = Modifier.weight(1.3f),
                )
                WheelPicker(
                    items = MONTHS,
                    selectedIndex = MONTHS.indexOf(state.birthMonth).coerceAtLeast(0),
                    onSelect = { i -> onPick(state.birthYear, MONTHS[i], state.birthDay) },
                    suffix = "월",
                    modifier = Modifier.weight(1f),
                )
                WheelPicker(
                    items = DAYS,
                    selectedIndex = DAYS.indexOf(state.birthDay).coerceAtLeast(0),
                    onSelect = { i -> onPick(state.birthYear, state.birthMonth, DAYS[i]) },
                    suffix = "일",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            "🔒 정보는 외부로 전송되지 않습니다",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = FortuneColors.textTertiary,
            textAlign = TextAlign.Center,
        )
    }
}
