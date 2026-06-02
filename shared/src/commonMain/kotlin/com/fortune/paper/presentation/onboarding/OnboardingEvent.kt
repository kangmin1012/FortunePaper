package com.fortune.paper.presentation.onboarding

import com.fortune.paper.core.mvi.ViewEvent

sealed interface OnboardingEvent : ViewEvent {
    /** 온보딩 완료 또는 기존 유저 로그인 → 메인으로 이동 */
    data object NavigateToMain : OnboardingEvent
    data class ShowError(val message: String) : OnboardingEvent
}
