package com.fortune.paper.presentation.onboarding.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.onboarding.OnboardingDependencies
import com.fortune.paper.presentation.onboarding.OnboardingEvent
import com.fortune.paper.presentation.onboarding.OnboardingState
import com.fortune.paper.presentation.onboarding.OnboardingStep

/**
 * Welcome 단계의 카카오 로그인.
 * - 기존 유저(프로필 있음) → NavigateToMain (온보딩 스킵)
 * - 신규 유저(프로필 없음) → Value 단계로 진행
 */
data object OnboardingLoginAction : ViewAction<OnboardingState, OnboardingEvent, OnboardingDependencies>() {
    override suspend fun execute(
        dependencies: OnboardingDependencies,
        scope: ActionScope<OnboardingState, OnboardingEvent>
    ) {
        scope.setState { copy(isAuthenticating = true, error = null) }

        val tokenResult = dependencies.kakaoAuth.login()
        tokenResult.onFailure { e ->
            scope.setState { copy(isAuthenticating = false, error = e.message ?: "카카오 로그인에 실패했습니다") }
            return
        }

        val token = tokenResult.getOrThrow()
        dependencies.userRepository.loginWithKakao(token.accessToken)
            .onSuccess { needsOnboarding ->
                scope.setState { copy(isAuthenticating = false, kakaoId = token.userId.toString()) }
                if (needsOnboarding) {
                    scope.setState { copy(step = OnboardingStep.Value) }
                } else {
                    scope.sendEvent(OnboardingEvent.NavigateToMain)
                }
            }
            .onFailure { e ->
                scope.setState { copy(isAuthenticating = false, error = e.message ?: "로그인에 실패했습니다") }
            }
    }
}
