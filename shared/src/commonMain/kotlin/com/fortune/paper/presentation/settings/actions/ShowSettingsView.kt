package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState
import com.fortune.paper.presentation.settings.SettingsView

/**
 * 설정 내부 화면 전환.
 * 편집 화면 진입 시 초안을 현재 프로필 값으로 시드한다 — 뒤로가기 시 편집 중 값은 폐기된다.
 */
data class ShowSettingsView(val view: SettingsView) :
    ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {

    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        val profile = scope.currentState.profile
        scope.setState {
            when (view) {
                SettingsView.ProfileEdit -> {
                    val birth = profile?.birthDate?.split("-")
                    copy(
                        view = view,
                        draftName = profile?.name.orEmpty(),
                        draftBirthYear = birth?.getOrNull(0)?.toIntOrNull() ?: 1995,
                        draftBirthMonth = birth?.getOrNull(1)?.toIntOrNull() ?: 1,
                        draftBirthDay = birth?.getOrNull(2)?.toIntOrNull() ?: 1,
                        draftGender = profile?.gender,
                        draftBirthTime = profile?.birthTime,
                        error = null,
                    )
                }
                SettingsView.NotifyEdit -> copy(
                    view = view,
                    draftNotifyTime = profile?.notifyTime ?: draftNotifyTime,
                    error = null,
                )
                SettingsView.List -> copy(view = view, error = null)
            }
        }
    }
}
