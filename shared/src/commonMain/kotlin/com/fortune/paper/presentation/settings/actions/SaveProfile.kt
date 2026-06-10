package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState
import com.fortune.paper.presentation.settings.SettingsView

/**
 * 내 정보 "저장하기".
 * 사주 입력값(생년월일·성별·시진) 변경 시 캐시 무효화는 SaveProfileUseCase가 처리한다.
 */
data object SaveProfile : ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {
    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        val state = scope.currentState
        val gender = state.draftGender
        if (!state.canSaveProfile || gender == null) {
            scope.sendEvent(SettingsEvent.ShowError("필수 정보가 누락되었습니다"))
            return
        }

        scope.setState { copy(isSaving = true, error = null) }

        dependencies.saveProfile(
            name = state.draftName.trim(),
            birthDate = state.draftBirthDateText,
            gender = gender,
            birthTime = state.draftBirthTime,
        ).onSuccess {
            val profile = dependencies.userRepository.getProfile()
            scope.setState {
                copy(isSaving = false, profile = profile, view = SettingsView.List)
            }
        }.onFailure { e ->
            scope.setState { copy(isSaving = false, error = e.message ?: "저장에 실패했습니다") }
            scope.sendEvent(SettingsEvent.ShowError(e.message ?: "저장에 실패했습니다"))
        }
    }
}
