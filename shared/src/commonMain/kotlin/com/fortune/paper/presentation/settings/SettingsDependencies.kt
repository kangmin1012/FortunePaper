package com.fortune.paper.presentation.settings

import com.fortune.paper.core.toad.ActionDependencies
import com.fortune.paper.domain.repository.UserRepository
import com.fortune.paper.domain.usecase.ResetAppDataUseCase
import com.fortune.paper.domain.usecase.SaveProfileUseCase
import com.fortune.paper.domain.usecase.UpdateNotifyTimeUseCase
import com.fortune.paper.platform.notification.LocalNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SettingsDependencies(
    override val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    val userRepository: UserRepository,
    val saveProfile: SaveProfileUseCase,
    val updateNotifySettings: UpdateNotifyTimeUseCase,
    val resetAppData: ResetAppDataUseCase,
    val notifier: LocalNotifier,
) : ActionDependencies()
