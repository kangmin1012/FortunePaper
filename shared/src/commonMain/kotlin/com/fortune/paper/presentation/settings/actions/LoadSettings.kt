package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState
import com.fortune.paper.core.toad.ViewAction

/** 설정 진입 시 현재 프로필·알림 설정을 로드한다. */
data object LoadSettings : ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {
    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        val profile = dependencies.userRepository.getProfile()
        scope.setState {
            copy(
                isLoading = false,
                profile = profile,
                notifyEnabled = profile?.notifyEnabled ?: true,
            )
        }
    }
}
