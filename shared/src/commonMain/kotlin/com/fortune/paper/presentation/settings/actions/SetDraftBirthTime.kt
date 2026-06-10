package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState

/** null = "잘 모르겠어요" (정오 대표값 처리) */
data class SetDraftBirthTime(val birthTime: String?) :
    ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {

    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        scope.setState { copy(draftBirthTime = birthTime) }
    }
}
