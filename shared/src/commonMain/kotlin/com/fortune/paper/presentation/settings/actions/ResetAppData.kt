package com.fortune.paper.presentation.settings.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsEvent
import com.fortune.paper.presentation.settings.SettingsState

/**
 * 정보 초기화 — 로컬 데이터 전체 삭제 + 알림 취소.
 * 삭제되면 App의 observeProfile Flow가 null을 방출해 자동으로 온보딩으로 복귀한다.
 */
data object ResetAppData : ViewAction<SettingsState, SettingsEvent, SettingsDependencies>() {
    override suspend fun execute(
        dependencies: SettingsDependencies,
        scope: ActionScope<SettingsState, SettingsEvent>
    ) {
        scope.setState { copy(showResetDialog = false, isSaving = true) }

        dependencies.notifier.cancel()
        dependencies.resetAppData()
            .onFailure { e ->
                scope.setState { copy(isSaving = false) }
                scope.sendEvent(SettingsEvent.ShowError(e.message ?: "초기화에 실패했습니다"))
            }
        // 성공 시 별도 처리 없음 — observeProfile Flow 분기로 온보딩 이동
    }
}
