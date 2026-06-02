package com.fortune.paper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fortune.paper.data.remote.SupabaseClientProvider
import com.fortune.paper.domain.repository.UserRepository
import com.fortune.paper.presentation.onboarding.OnboardingScreen
import com.fortune.paper.presentation.theme.AppTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import org.koin.compose.koinInject

@Composable
fun App() {
    AppTheme {
        val sessionStatus by SupabaseClientProvider.client.auth.sessionStatus
            .collectAsStateWithLifecycle()
        val userRepository = koinInject<UserRepository>()

        // 온보딩 완료 여부. 기존 프로필이 있으면 메인으로, 없으면 온보딩을 진행한다.
        var onboarded by remember { mutableStateOf(false) }
        var profileProbed by remember { mutableStateOf(false) }

        LaunchedEffect(sessionStatus) {
            when (sessionStatus) {
                is SessionStatus.Authenticated -> {
                    if (!onboarded && !profileProbed) {
                        profileProbed = true
                        userRepository.getCurrentUser().onSuccess { onboarded = true }
                    }
                }
                is SessionStatus.NotAuthenticated -> {
                    onboarded = false
                    profileProbed = false
                }
                else -> Unit
            }
        }

        when {
            sessionStatus is SessionStatus.Initializing -> LoadingScreen()
            onboarded -> MainPlaceholder()
            else -> OnboardingScreen(onComplete = { onboarded = true })
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MainPlaceholder() {
    // Task 5에서 리포트 화면으로 교체 예정
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("온보딩 완료! 리포트 화면은 Task 5에서 구현됩니다.")
    }
}
