package com.fortune.paper.presentation.report.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.domain.model.FortuneRateLimitedException
import com.fortune.paper.presentation.report.ReportDependencies
import com.fortune.paper.presentation.report.ReportEvent
import com.fortune.paper.presentation.report.ReportState

/** 한도 초과 시 다이얼로그를 닫은 뒤에도 화면에 남는 인라인 안내 문구 (재시도 버튼과 함께). */
internal const val OUT_OF_PAPER_INLINE = "오늘은 용지가 다 떨어졌어요 📄\n내일 다시 만나요!"

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

        scope.setState { copy(isLoading = true, error = null, outOfPaper = false) }
        dependencies.getTodayReport()
            .onSuccess { report ->
                scope.setState { copy(isLoading = false, report = report, error = null) }
            }
            .onFailure { e ->
                if (e is FortuneRateLimitedException) {
                    // 무료 티어 한도 초과 → "용지 소진" 다이얼로그 + 닫은 뒤에도 남을 인라인 안내(재시도 버튼).
                    scope.setState {
                        copy(isLoading = false, outOfPaper = true, error = OUT_OF_PAPER_INLINE)
                    }
                } else {
                    val message = e.message ?: "리포트를 불러오지 못했어요"
                    scope.setState { copy(isLoading = false, error = message) }
                    scope.sendEvent(ReportEvent.ShowError(message))
                }
            }
    }
}
