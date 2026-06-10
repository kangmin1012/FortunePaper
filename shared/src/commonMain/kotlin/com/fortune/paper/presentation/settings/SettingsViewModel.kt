package com.fortune.paper.presentation.settings

import com.fortune.paper.core.toad.ToadViewModel

class SettingsViewModel(deps: SettingsDependencies) :
    ToadViewModel<SettingsState, SettingsEvent>(
        initialState = SettingsState(),
        dependencies = deps
    )
