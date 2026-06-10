package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState

data class SetDraftGender(val gender: Gender) :
    ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {

    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        scope.setState { copy(draftGender = gender) }
    }
}
