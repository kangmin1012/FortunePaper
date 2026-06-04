package com.fortune.paper.presentation.report

import com.fortune.paper.core.toad.ActionDependencies
import com.fortune.paper.domain.usecase.GetTodayReportUseCase
import com.fortune.paper.domain.usecase.RefreshReportUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ReportDependencies(
    override val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    val getTodayReport: GetTodayReportUseCase,
    val refreshReport: RefreshReportUseCase,
) : ActionDependencies()
