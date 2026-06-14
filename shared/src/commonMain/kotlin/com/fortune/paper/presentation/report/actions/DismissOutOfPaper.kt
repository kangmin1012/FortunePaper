package com.fortune.paper.presentation.report.actions

import com.fortune.paper.core.toad.ActionScope
import com.fortune.paper.core.toad.ViewAction
import com.fortune.paper.presentation.report.ReportDependencies
import com.fortune.paper.presentation.report.ReportEvent
import com.fortune.paper.presentation.report.ReportState

/** "오늘 용지가 다 떨어졌어요" 다이얼로그를 닫는다. */
data object DismissOutOfPaper : ViewAction<ReportState, ReportEvent, ReportDependencies>() {
    override suspend fun execute(
        dependencies: ReportDependencies,
        scope: ActionScope<ReportState, ReportEvent>,
    ) {
        scope.setState { copy(outOfPaper = false) }
    }
}
