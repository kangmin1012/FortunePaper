package com.fortune.paper.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fortune.paper.presentation.onboarding.actions.GoToNextStep
import com.fortune.paper.presentation.onboarding.actions.GoToPreviousStep
import com.fortune.paper.presentation.onboarding.actions.SetBirthDate
import com.fortune.paper.presentation.onboarding.actions.SetBirthTime
import com.fortune.paper.presentation.onboarding.actions.SetGender
import com.fortune.paper.presentation.onboarding.actions.SetName
import com.fortune.paper.presentation.onboarding.actions.SetNotifyTime
import com.fortune.paper.presentation.onboarding.actions.SubmitOnboarding
import com.fortune.paper.presentation.onboarding.components.FPButton
import com.fortune.paper.presentation.onboarding.components.StepShell
import com.fortune.paper.presentation.onboarding.screens.BirthStep
import com.fortune.paper.presentation.onboarding.screens.GenderStep
import com.fortune.paper.presentation.onboarding.screens.NameStep
import com.fortune.paper.presentation.onboarding.screens.NotifyStep
import com.fortune.paper.presentation.onboarding.screens.TimeStep
import com.fortune.paper.presentation.onboarding.screens.ValueStep
import com.fortune.paper.presentation.onboarding.screens.WelcomeStep
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                OnboardingEvent.NavigateToMain -> onComplete()
                is OnboardingEvent.ShowError -> { /* 인라인 state.error 로 표시 */ }
            }
        }
    }

    if (state.step == OnboardingStep.Welcome) {
        WelcomeStep(
            state = state,
            onStart = { viewModel.dispatch(GoToNextStep) },
        )
        return
    }

    val isNotify = state.step == OnboardingStep.Notify
    StepShell(
        progress = state.step.progress,
        onBack = { viewModel.dispatch(GoToPreviousStep) },
        onSkip = if (state.step == OnboardingStep.Time) {
            {
                viewModel.dispatch(SetBirthTime(null))
                viewModel.dispatch(GoToNextStep)
            }
        } else null,
        footer = {
            FPButton(
                text = if (isNotify) "완료하기" else "다음",
                enabled = state.canProceed,
                loading = state.isSubmitting,
                onClick = {
                    if (isNotify) viewModel.dispatch(SubmitOnboarding)
                    else viewModel.dispatch(GoToNextStep)
                },
            )
        },
    ) {
        when (state.step) {
            OnboardingStep.Value -> ValueStep()
            OnboardingStep.Name -> NameStep(state) { viewModel.dispatch(SetName(it)) }
            OnboardingStep.Birth -> BirthStep(state) { y, m, d -> viewModel.dispatch(SetBirthDate(y, m, d)) }
            OnboardingStep.Gender -> GenderStep(state.gender) { viewModel.dispatch(SetGender(it)) }
            OnboardingStep.Time -> TimeStep(state) { viewModel.dispatch(SetBirthTime(it)) }
            OnboardingStep.Notify -> NotifyStep(state.notifyTime) { viewModel.dispatch(SetNotifyTime(it)) }
            OnboardingStep.Welcome -> Unit
        }
    }
}
