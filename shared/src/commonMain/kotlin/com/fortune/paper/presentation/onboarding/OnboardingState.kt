package com.fortune.paper.presentation.onboarding

import com.fortune.paper.core.mvi.ViewState
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.domain.model.UserProfile

data class OnboardingState(
    val step: OnboardingStep = OnboardingStep.Welcome,
    val isSubmitting: Boolean = false,
    val name: String = "",
    val birthYear: Int = 1995,
    val birthMonth: Int = 1,
    val birthDay: Int = 1,
    val gender: Gender? = null,
    /** 태어난 시각 12지지 한 글자(자~해). null이면 미선택/모름 (정오 대표값) */
    val birthTime: String? = null,
    val notifyTime: String = UserProfile.DEFAULT_NOTIFY_TIME,
    val error: String? = null,
) : ViewState {

    /** 현재 단계에서 "다음/완료" 진행이 가능한지 */
    val canProceed: Boolean
        get() = when (step) {
            OnboardingStep.Welcome -> true // "시작하기" (v1.1 — 로그인 없음)
            OnboardingStep.Value -> true
            OnboardingStep.Name -> name.trim().length in 1..MAX_NAME_LENGTH
            OnboardingStep.Birth -> true
            OnboardingStep.Gender -> gender != null
            OnboardingStep.Time -> true // 선택 입력
            OnboardingStep.Notify -> notifyTime.isNotBlank()
        }

    /** "YYYY-MM-DD" 포맷 생년월일 */
    val birthDateText: String
        get() = "$birthYear-${birthMonth.pad2()}-${birthDay.pad2()}"

    private fun Int.pad2(): String = toString().padStart(2, '0')

    companion object {
        const val MAX_NAME_LENGTH = 12
        const val DEFAULT_NOTIFY_TIME = UserProfile.DEFAULT_NOTIFY_TIME
    }
}
