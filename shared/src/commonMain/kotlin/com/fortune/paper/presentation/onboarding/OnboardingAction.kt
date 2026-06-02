package com.fortune.paper.presentation.onboarding

import com.fortune.paper.core.toad.ViewAction

/** 온보딩 화면에서 dispatch 가능한 액션 타입. */
typealias OnboardingAction = ViewAction<OnboardingState, OnboardingEvent, OnboardingDependencies>
