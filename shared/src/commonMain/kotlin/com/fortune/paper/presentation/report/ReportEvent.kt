package com.fortune.paper.presentation.report

import com.fortune.paper.core.mvi.ViewEvent

sealed interface ReportEvent : ViewEvent {
    data class ShowError(val message: String) : ReportEvent
    data object NavigateToSettings : ReportEvent
}
