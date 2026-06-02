package com.fortune.paper.presentation.onboarding

/**
 * 첫 실행 온보딩 7단계.
 * 디자인 기준: design/FortunePaper_Design/screens.jsx 의 STEPS
 * = ['welcome', 'value', 'name', 'birth', 'gender', 'time', 'notify']
 */
enum class OnboardingStep {
    Welcome,
    Value,
    Name,
    Birth,
    Gender,
    Time,
    Notify;

    val isFirst: Boolean get() = this == Welcome
    val isLast: Boolean get() = this == Notify

    /** 진행 바 비율 (0.0 ~ 1.0) */
    val progress: Float get() = (ordinal + 1).toFloat() / entries.size

    fun next(): OnboardingStep = entries.getOrElse(ordinal + 1) { this }
    fun previous(): OnboardingStep = entries.getOrElse(ordinal - 1) { this }
}
