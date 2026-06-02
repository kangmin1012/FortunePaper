package com.fortune.paper.presentation.onboarding.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.presentation.onboarding.OnboardingDependencies
import com.fortune.paper.presentation.onboarding.OnboardingEvent
import com.fortune.paper.presentation.onboarding.OnboardingState

data class SetGender(val gender: Gender) : ViewAction<OnboardingState, OnboardingEvent, OnboardingDependencies>() {
    override suspend fun execute(
        dependencies: OnboardingDependencies,
        scope: ActionScope<OnboardingState, OnboardingEvent>
    ) {
        scope.setState { copy(gender = gender) }
    }
}
