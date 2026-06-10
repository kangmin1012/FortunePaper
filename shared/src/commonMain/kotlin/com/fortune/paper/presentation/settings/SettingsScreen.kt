package com.fortune.paper.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fortune.paper.presentation.settings.actions.LoadSettings
import com.fortune.paper.presentation.settings.actions.ResetAppData
import com.fortune.paper.presentation.settings.actions.SaveProfile
import com.fortune.paper.presentation.settings.actions.SetDraftBirth
import com.fortune.paper.presentation.settings.actions.SetDraftBirthTime
import com.fortune.paper.presentation.settings.actions.SetDraftGender
import com.fortune.paper.presentation.settings.actions.SetDraftName
import com.fortune.paper.presentation.settings.actions.SetDraftNotifyTime
import com.fortune.paper.presentation.settings.actions.SetResetDialog
import com.fortune.paper.presentation.settings.actions.ShowSettingsView
import com.fortune.paper.presentation.settings.actions.ToggleNotify
import com.fortune.paper.presentation.settings.actions.UpdateNotifyTime
import com.fortune.paper.presentation.settings.components.NotifyEditContent
import com.fortune.paper.presentation.settings.components.ProfileEditContent
import com.fortune.paper.presentation.settings.components.ResetConfirmDialog
import com.fortune.paper.presentation.settings.components.SettingsListContent
import com.fortune.paper.presentation.settings.components.SettingsShell
import com.fortune.paper.presentation.settings.components.SettingsTabBar
import com.fortune.paper.presentation.settings.components.notifyTimeLabel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) { viewModel.dispatch(LoadSettings) }

    when (state.view) {
        SettingsView.List -> SettingsShell(
            title = "설정",
            onBack = onBack,
            bottomBar = { SettingsTabBar(onTabToday = onBack) },
        ) {
            SettingsListContent(
                profileName = state.profile?.name.orEmpty(),
                notifyMeta = if (state.notifyEnabled) {
                    "매일 ${notifyTimeLabel(state.profile?.notifyTime ?: state.draftNotifyTime)}"
                } else {
                    "꺼짐"
                },
                onOpenProfile = { viewModel.dispatch(ShowSettingsView(SettingsView.ProfileEdit)) },
                onOpenNotify = { viewModel.dispatch(ShowSettingsView(SettingsView.NotifyEdit)) },
                onReset = { viewModel.dispatch(SetResetDialog(true)) },
            )
        }

        SettingsView.ProfileEdit -> SettingsShell(
            title = "내 정보",
            onBack = { viewModel.dispatch(ShowSettingsView(SettingsView.List)) },
        ) {
            ProfileEditContent(
                state = state,
                onNameChange = { viewModel.dispatch(SetDraftName(it)) },
                onBirthChange = { y, m, d -> viewModel.dispatch(SetDraftBirth(y, m, d)) },
                onGenderChange = { viewModel.dispatch(SetDraftGender(it)) },
                onBirthTimeChange = { viewModel.dispatch(SetDraftBirthTime(it)) },
                onSave = { viewModel.dispatch(SaveProfile) },
            )
        }

        SettingsView.NotifyEdit -> SettingsShell(
            title = "알림 설정",
            onBack = { viewModel.dispatch(ShowSettingsView(SettingsView.List)) },
        ) {
            NotifyEditContent(
                state = state,
                onToggle = { viewModel.dispatch(ToggleNotify(it)) },
                onSelectTime = { viewModel.dispatch(SetDraftNotifyTime(it)) },
                onSave = { viewModel.dispatch(UpdateNotifyTime) },
            )
        }
    }

    if (state.showResetDialog) {
        ResetConfirmDialog(
            onConfirm = { viewModel.dispatch(ResetAppData) },
            onCancel = { viewModel.dispatch(SetResetDialog(false)) },
        )
    }
}
