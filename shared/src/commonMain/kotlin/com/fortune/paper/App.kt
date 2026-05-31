package com.fortune.paper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fortune.paper.data.remote.SupabaseClientProvider
import com.fortune.paper.presentation.login.LoginScreen
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun App() {
    MaterialTheme {
        val sessionStatus by SupabaseClientProvider.client.auth.sessionStatus
            .collectAsStateWithLifecycle()

        when (sessionStatus) {
            is SessionStatus.Authenticated -> {
                // Task 5에서 온보딩/리포트 화면 분기 구현 예정
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("로그인 완료! 리포트 화면은 Task 5에서 구현됩니다.")
                }
            }
            is SessionStatus.NotAuthenticated,
            is SessionStatus.RefreshFailure -> LoginScreen()
            SessionStatus.Initializing -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
