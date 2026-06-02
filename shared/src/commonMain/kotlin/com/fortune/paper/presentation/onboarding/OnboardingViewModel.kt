package com.fortune.paper.presentation.onboarding

import com.fortune.paper.core.toad.ToadViewModel

class OnboardingViewModel(deps: OnboardingDependencies) :
    ToadViewModel<OnboardingState, OnboardingEvent>(
        initialState = OnboardingState(),
        dependencies = deps
    )
