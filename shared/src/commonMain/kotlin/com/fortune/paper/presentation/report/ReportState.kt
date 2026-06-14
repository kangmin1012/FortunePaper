package com.fortune.paper.presentation.report

import com.fortune.paper.core.mvi.ViewState
import com.fortune.paper.domain.model.FortuneReport

data class ReportState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val report: FortuneReport? = null,
    val error: String? = null,
    /** Gemini 무료 티어 한도 초과(429) — "오늘 용지가 다 떨어졌어요" 다이얼로그 표시 여부. */
    val outOfPaper: Boolean = false,
) : ViewState
