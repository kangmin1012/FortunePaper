package com.fortune.paper.presentation.onboarding.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.platform.notification.parseNotifyTime
import com.fortune.paper.presentation.onboarding.OnboardingDependencies
import com.fortune.paper.presentation.onboarding.OnboardingEvent
import com.fortune.paper.presentation.onboarding.OnboardingState

/**
 * 마지막 단계(Notify)의 "완료하기".
 * 수집한 프로필을 로컬(DataStore)에 저장하고, 알림 권한 요청 + 로컬 알림을 예약한 뒤
 * 메인으로 이동한다. 권한이 거부되어도 온보딩은 완료 처리한다 (PRD §4.4).
 */
data object SubmitOnboarding : ViewAction<OnboardingState, OnboardingEvent, OnboardingDependencies>() {
    override suspend fun execute(
        dependencies: OnboardingDependencies,
        scope: ActionScope<OnboardingState, OnboardingEvent>
    ) {
        val state = scope.currentState
        val gender = state.gender
        if (state.name.trim().isEmpty() || gender == null) {
            scope.sendEvent(OnboardingEvent.ShowError("필수 정보가 누락되었습니다"))
            return
        }

        scope.setState { copy(isSubmitting = true, error = null) }

        dependencies.saveProfile(
            name = state.name.trim(),
            birthDate = state.birthDateText,
            gender = gender,
            birthTime = state.birthTime,
        ).onSuccess {
            // 알림 설정 저장 + 권한 요청 + 예약 (실패해도 온보딩 자체는 완료 처리)
            dependencies.updateNotifySettings(enabled = true, time = state.notifyTime)
            val granted = dependencies.notifier.requestPermission()
            if (granted) {
                val (hour, minute) = parseNotifyTime(state.notifyTime)
                dependencies.notifier.scheduleDaily(hour, minute)
            }
            scope.setState { copy(isSubmitting = false) }
            scope.sendEvent(OnboardingEvent.NavigateToMain)
        }.onFailure { e ->
            scope.setState { copy(isSubmitting = false, error = e.message ?: "저장에 실패했습니다") }
            scope.sendEvent(OnboardingEvent.ShowError(e.message ?: "저장에 실패했습니다"))
        }
    }
}
