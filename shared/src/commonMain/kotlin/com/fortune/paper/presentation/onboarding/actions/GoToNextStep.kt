package com.fortune.paper.presentation.onboarding.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.onboarding.OnboardingDependencies
import com.fortune.paper.presentation.onboarding.OnboardingEvent
import com.fortune.paper.presentation.onboarding.OnboardingState

/** 현재 단계가 유효하면 다음 단계로 이동. (마지막 단계 완료는 SubmitOnboarding이 담당) */
data object GoToNextStep : ViewAction<OnboardingState, OnboardingEvent, OnboardingDependencies>() {
    override suspend fun execute(
        dependencies: OnboardingDependencies,
        scope: ActionScope<OnboardingState, OnboardingEvent>
    ) {
        val state = scope.currentState
        if (!state.canProceed || state.step.isLast) return
        scope.setState { copy(step = step.next(), error = null) }
    }
}
