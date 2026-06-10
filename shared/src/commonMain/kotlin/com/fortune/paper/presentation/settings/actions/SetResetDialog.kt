package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState

/** 정보 초기화 확인 다이얼로그 표시/숨김. */
data class SetResetDialog(val visible: Boolean) :
    ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {

    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        scope.setState { copy(showResetDialog = visible) }
    }
}
