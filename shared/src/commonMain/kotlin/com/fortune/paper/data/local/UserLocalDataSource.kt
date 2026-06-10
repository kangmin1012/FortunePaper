package com.fortune.paper.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** 프로필 + 알림 설정 read/write. 키 스키마 → .claude/rules/architecture.md */
class UserLocalDataSource(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val NAME = stringPreferencesKey("profile_name")
        val BIRTH_DATE = stringPreferencesKey("profile_birth_date")
        val GENDER = stringPreferencesKey("profile_gender")
        val BIRTH_TIME = stringPreferencesKey("profile_birth_time")
        val NOTIFY_ENABLED = booleanPreferencesKey("notify_enabled")
        val NOTIFY_TIME = stringPreferencesKey("notify_time")
    }

    /** 필수 3종(name·birthDate·gender)이 모두 있어야 프로필로 인정, 아니면 null */
    fun observeProfile(): Flow<UserProfile?> = dataStore.data.map { it.toProfile() }

    suspend fun getProfile(): UserProfile? = dataStore.data.first().toProfile()

    suspend fun saveProfile(name: String, birthDate: String, gender: Gender, birthTime: String?) {
        dataStore.edit { prefs ->
            prefs[Keys.NAME] = name
            prefs[Keys.BIRTH_DATE] = birthDate
            prefs[Keys.GENDER] = gender.name
            if (birthTime != null) prefs[Keys.BIRTH_TIME] = birthTime
            else prefs.remove(Keys.BIRTH_TIME)
        }
    }

    suspend fun updateNotifySettings(enabled: Boolean, time: String) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFY_ENABLED] = enabled
            prefs[Keys.NOTIFY_TIME] = time
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    private fun Preferences.toProfile(): UserProfile? {
        val name = this[Keys.NAME] ?: return null
        val birthDate = this[Keys.BIRTH_DATE] ?: return null
        val gender = this[Keys.GENDER] ?: return null
        return UserProfile(
            name = name,
            birthDate = birthDate,
            gender = Gender.fromString(gender),
            birthTime = this[Keys.BIRTH_TIME],
            notifyEnabled = this[Keys.NOTIFY_ENABLED] ?: true,
            notifyTime = this[Keys.NOTIFY_TIME] ?: UserProfile.DEFAULT_NOTIFY_TIME,
        )
    }
}
