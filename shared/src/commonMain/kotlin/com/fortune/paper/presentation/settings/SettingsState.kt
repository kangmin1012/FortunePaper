package com.fortune.paper.presentation.settings

import com.fortune.paper.core.mvi.ViewState
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.domain.model.UserProfile

/** 설정 내부 화면 — 목록 / 내 정보 편집 / 알림 설정 편집 */
enum class SettingsView { List, ProfileEdit, NotifyEdit }

data class SettingsState(
    val view: SettingsView = SettingsView.List,
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val notifyEnabled: Boolean = true,
    // 편집 초안 — 편집 화면 진입 시 profile에서 시드되고, 뒤로가기 시 폐기된다
    val draftName: String = "",
    val draftBirthYear: Int = 1995,
    val draftBirthMonth: Int = 1,
    val draftBirthDay: Int = 1,
    val draftGender: Gender? = null,
    val draftBirthTime: String? = null,
    val draftNotifyTime: String = UserProfile.DEFAULT_NOTIFY_TIME,
    val isSaving: Boolean = false,
    val showResetDialog: Boolean = false,
    val error: String? = null,
) : ViewState {

    val canSaveProfile: Boolean
        get() = draftName.trim().length in 1..MAX_NAME_LENGTH && draftGender != null

    /** "YYYY-MM-DD" 포맷 초안 생년월일 */
    val draftBirthDateText: String
        get() = "$draftBirthYear-${draftBirthMonth.pad2()}-${draftBirthDay.pad2()}"

    private fun Int.pad2(): String = toString().padStart(2, '0')

    companion object {
        const val MAX_NAME_LENGTH = 12
    }
}
