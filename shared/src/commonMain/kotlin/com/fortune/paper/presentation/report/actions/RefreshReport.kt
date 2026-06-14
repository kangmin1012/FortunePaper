package com.fortune.paper.presentation.report.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.domain.model.FortuneRateLimitedException
import com.fortune.paper.presentation.report.ReportDependencies
import com.fortune.paper.presentation.report.ReportEvent
import com.fortune.paper.presentation.report.ReportState

/**
 * 수동 새로고침. 당일 재생성은 하지 않고 캐시를 다시 반환한다 (PRD §4.3).
 * 실패해도 기존 리포트는 유지한다.
 */
data object RefreshReport : ViewAction<ReportState, ReportEvent, ReportDependencies>() {
    override suspend fun execute(
        dependencies: ReportDependencies,
        scope: ActionScope<ReportState, ReportEvent>,
    ) {
        scope.setState { copy(isRefreshing = true, error = null, outOfPaper = false) }
        dependencies.refreshReport()
            .onSuccess { report ->
                scope.setState { copy(isRefreshing = false, report = report) }
            }
            .onFailure { e ->
                scope.setState { copy(isRefreshing = false) }
                if (e is FortuneRateLimitedException) {
                    // 리포트가 없을 때만 발생(캐시 있으면 재호출 안 함) → 다이얼로그 + 인라인 안내.
                    scope.setState { copy(outOfPaper = true, error = OUT_OF_PAPER_INLINE) }
                } else {
                    scope.sendEvent(ReportEvent.ShowError(e.message ?: "새로고침에 실패했어요"))
                }
            }
    }
}
