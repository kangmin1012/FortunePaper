package com.fortune.paper.presentation.settings

import com.fortune.paper.core.toad.ViewAction

/** 설정 화면에서 dispatch 가능한 액션 타입. */
typealias SettingsAction = ViewAction<SettingsState, SettingsEvent, SettingsDependencies>
