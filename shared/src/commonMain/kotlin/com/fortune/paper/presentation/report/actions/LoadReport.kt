package com.fortune.paper.presentation.report.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.report.ReportDependencies
import com.fortune.paper.presentation.report.ReportEvent
import com.fortune.paper.presentation.report.ReportState

/**
 * 화면 진입 시 오늘의 리포트 로드.
 * 캐시가 있으면 즉시, 없으면 Edge Function 으로 생성한다 (UseCase 위임).
 */
data object LoadReport : ViewAction<ReportState, ReportEvent, ReportDependencies>() {
    override suspend fun execute(
        dependencies: ReportDependencies,
        scope: ActionScope<ReportState, ReportEvent>,
    ) {
        // 이미 로드된 경우 재요청하지 않는다 (당일 1회 생성).
        if (scope.currentState.report != null) return

        scope.setState { copy(isLoading = true, error = null) }
        dependencies.getTodayReport()
            .onSuccess { report ->
                scope.setState { copy(isLoading = false, report = report, error = null) }
            }
            .onFailure { e ->
                val message = e.message ?: "리포트를 불러오지 못했어요"
                scope.setState { copy(isLoading = false, error = message) }
                scope.sendEvent(ReportEvent.ShowError(message))
            }
    }
}
