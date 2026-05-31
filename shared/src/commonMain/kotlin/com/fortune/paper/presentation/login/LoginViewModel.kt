package com.fortune.paper.presentation.login

import com.fortune.paper.core.toad.ToadViewModel

class LoginViewModel(deps: LoginDependencies) :
    ToadViewModel<LoginState, LoginEvent>(
        initialState = LoginState(),
        dependencies = deps
    )
