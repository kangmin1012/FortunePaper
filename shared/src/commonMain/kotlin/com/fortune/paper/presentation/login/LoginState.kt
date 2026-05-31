package com.fortune.paper.presentation.login

import com.fortune.paper.core.mvi.ViewState

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
) : ViewState
