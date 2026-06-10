package com.fortune.paper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fortune.paper.domain.repository.UserRepository
import com.fortune.paper.presentation.onboarding.OnboardingScreen
import com.fortune.paper.presentation.report.ReportScreen
import com.fortune.paper.presentation.settings.SettingsScreen
import com.fortune.paper.presentation.theme.AppTheme
import org.koin.compose.koinInject

/** 앱 시작 분기 상태 — 로컬 프로필 로드 전(Loading) / 부재(Onboarding) / 존재(Main) */
private enum class ProfileGate { Loading, Missing, Present }

@Composable
fun App() {
    AppTheme {
        val userRepository = koinInject<UserRepository>()

        // 로컬 프로필(필수 3종) 존재 여부로 분기한다 (v1.1 — 세션 없음).
        // 설정의 "정보 초기화" 시 Flow가 null을 방출해 자동으로 온보딩으로 복귀한다.
        var gate by remember { mutableStateOf(ProfileGate.Loading) }
        var showSettings by remember { mutableStateOf(false) }

        LaunchedEffect(userRepository) {
            userRepository.observeProfile().collect { profile ->
                val next = if (profile == null) ProfileGate.Missing else ProfileGate.Present
                if (next == ProfileGate.Missing) showSettings = false
                gate = next
            }
        }

        when (gate) {
            ProfileGate.Loading -> LoadingScreen()
            ProfileGate.Missing -> OnboardingScreen(onComplete = { /* Flow 방출로 자동 전환 */ })
            ProfileGate.Present ->
                if (showSettings) {
                    SettingsScreen(onBack = { showSettings = false })
                } else {
                    ReportScreen(onNavigateToSettings = { showSettings = true })
                }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
