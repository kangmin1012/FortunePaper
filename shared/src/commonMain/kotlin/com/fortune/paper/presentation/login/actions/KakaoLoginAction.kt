package com.fortune.paper.presentation.login.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.login.LoginDependencies
import com.fortune.paper.presentation.login.LoginEvent
import com.fortune.paper.presentation.login.LoginState

data object KakaoLoginAction : ViewAction<LoginState, LoginEvent, LoginDependencies>() {
    override suspend fun execute(
        dependencies: LoginDependencies,
        scope: ActionScope<LoginState, LoginEvent>
    ) {
        scope.setState { copy(isLoading = true, error = null) }

        val kakaoResult = dependencies.kakaoAuth.login()
        kakaoResult.onFailure { e ->
            scope.setState { copy(isLoading = false, error = e.message ?: "카카오 로그인에 실패했습니다") }
            return
        }

        val token = kakaoResult.getOrThrow()
        dependencies.userRepository.loginWithKakao(token.accessToken)
            .onSuccess {
                scope.setState { copy(isLoading = false) }
                // Supabase sessionStatus가 Authenticated로 변경되면 App.kt가 자동으로 화면 전환
            }
            .onFailure { e ->
                scope.setState { copy(isLoading = false, error = e.message ?: "로그인에 실패했습니다") }
            }
    }
}
