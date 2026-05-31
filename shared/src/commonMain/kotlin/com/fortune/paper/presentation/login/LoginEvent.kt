package com.fortune.paper.presentation.login

import com.fortune.paper.core.mvi.ViewEvent

sealed interface LoginEvent : ViewEvent {
    data class ShowError(val message: String) : LoginEvent
}
