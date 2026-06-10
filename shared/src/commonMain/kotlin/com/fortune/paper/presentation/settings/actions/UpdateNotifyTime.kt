package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.platform.notification.parseNotifyTime
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState
import com.fortune.paper.presentation.settings.SettingsView

/** 알림 시각 저장 — 즉시 저장하고 알림이 켜져 있으면 로컬 알림을 재예약한다. */
data object UpdateNotifyTime : ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {
    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        val state = scope.currentState
        scope.setState { copy(isSaving = true, error = null) }

        dependencies.updateNotifySettings(
            enabled = state.notifyEnabled,
            time = state.draftNotifyTime,
        ).onSuccess {
            if (state.notifyEnabled) {
                val (hour, minute) = parseNotifyTime(state.draftNotifyTime)
                dependencies.notifier.scheduleDaily(hour, minute)
            }
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
