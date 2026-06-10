package com.fortune.paper.presentation.settings

import com.fortune.paper.core.mvi.ViewEvent

sealed interface SettingsEvent : ViewEvent {
    data class ShowError(val message: String) : SettingsEvent
}
