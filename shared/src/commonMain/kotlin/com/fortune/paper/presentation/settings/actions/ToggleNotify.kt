package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.platform.notification.parseNotifyTime
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState

/**
 * 알림 켜기/끄기 토글 — 즉시 적용.
 * 꺼도 notify_time은 보존되어 다시 켤 때 복원된다 (PRD §4.4).
 */
data class ToggleNotify(val enabled: Boolean) :
    ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {

    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        val state = scope.currentState
        val time = state.profile?.notifyTime ?: state.draftNotifyTime

        dependencies.updateNotifySettings(enabled = enabled, time = time)
            .onSuccess {
                if (enabled) {
                    val granted = dependencies.notifier.requestPermission()
                    if (granted) {
                        val (hour, minute) = parseNotifyTime(time)
                        dependencies.notifier.scheduleDaily(hour, minute)
                    }
                } else {
                    dependencies.notifier.cancel()
                }
                val profile = dependencies.userRepository.getProfile()
                scope.setState { copy(notifyEnabled = enabled, profile = profile) }
            }
            .onFailure { e ->
                scope.sendEvent(SettingsEvent.ShowError(e.message ?: "설정 변경에 실패했습니다"))
            }
    }
}
