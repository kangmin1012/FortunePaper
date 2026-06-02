package com.fortune.paper.presentation.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.fortune.paper.presentation.onboarding.OnboardingState
import com.fortune.paper.presentation.onboarding.components.GradeStrip
import com.fortune.paper.presentation.onboarding.components.HeroGradeIcon
import com.fortune.paper.presentation.onboarding.components.KakaoButton
import com.fortune.paper.presentation.theme.FortuneColors

@Composable
fun WelcomeStep(
    state: OnboardingState,
    onKakaoLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FortuneColors.bgPrimary)
            .safeContentPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(72.dp))

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "FORTUNEPAPER",
                style = MaterialTheme.typography.labelMedium,
                color = FortuneColors.textTertiary,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(24.dp))
            HeroGradeIcon()
            Spacer(Modifier.height(28.dp))
            Text(
                "오늘의 흐름을\n한 장에 담아 드릴게요",
                fontSize = 26.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FortuneColors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                "사주를 기반으로 매일 아침\n한 장의 운세 리포트를 보내 드립니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = FortuneColors.textTertiary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(22.dp))
            GradeStrip()
            Spacer(Modifier.height(10.dp))
            Text(
                "5단계 날씨 등급",
                style = MaterialTheme.typography.bodySmall,
                color = FortuneColors.textSecondary,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.error != null) {
                Text(
                    state.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = FortuneColors.error,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
            }
            KakaoButton(onClick = onKakaoLogin, loading = state.isAuthenticating)
            Spacer(Modifier.height(12.dp))
            Text(
                "계속하면 이용약관 · 개인정보 처리방침에 동의합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = FortuneColors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
