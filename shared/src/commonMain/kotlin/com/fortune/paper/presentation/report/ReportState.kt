package com.fortune.paper.presentation.report

import com.fortune.paper.core.mvi.ViewState
import com.fortune.paper.domain.model.FortuneReport

data class ReportState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val report: FortuneReport? = null,
    val error: String? = null,
) : ViewState
