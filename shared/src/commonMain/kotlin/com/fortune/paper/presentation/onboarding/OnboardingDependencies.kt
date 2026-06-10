package com.fortune.paper.presentation.onboarding

import com.fortune.paper.core.toad.ActionDependencies
import com.fortune.paper.domain.usecase.SaveProfileUseCase
import com.fortune.paper.domain.usecase.UpdateNotifyTimeUseCase
import com.fortune.paper.platform.notification.LocalNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class OnboardingDependencies(
    override val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    val saveProfile: SaveProfileUseCase,
    val updateNotifySettings: UpdateNotifyTimeUseCase,
    val notifier: LocalNotifier,
) : ActionDependencies()
