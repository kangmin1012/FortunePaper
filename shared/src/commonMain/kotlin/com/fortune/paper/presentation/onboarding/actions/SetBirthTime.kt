package com.fortune.paper.presentation.onboarding.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.onboarding.OnboardingDependencies
import com.fortune.paper.presentation.onboarding.OnboardingEvent
import com.fortune.paper.presentation.onboarding.OnboardingState

/** 태어난 시각(12지지 한 글자). null = 모름/미선택. */
data class SetBirthTime(val branch: String?) : ViewAction<OnboardingState, OnboardingEvent, OnboardingDependencies>() {
    override suspend fun execute(
        dependencies: OnboardingDependencies,
        scope: ActionScope<OnboardingState, OnboardingEvent>
    ) {
        scope.setState { copy(birthTime = branch) }
    }
}
