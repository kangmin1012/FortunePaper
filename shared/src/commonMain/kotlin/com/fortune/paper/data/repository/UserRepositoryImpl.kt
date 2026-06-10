package com.fortune.paper.data.repository

import com.fortune.paper.data.local.UserLocalDataSource
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.domain.model.UserProfile
import com.fortune.paper.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/** v1.1 — 전부 로컬(DataStore) 위임. 서버에 유저 데이터를 저장하지 않는다. */
class UserRepositoryImpl(
    private val userLocal: UserLocalDataSource,
) : UserRepository {

    override fun observeProfile(): Flow<UserProfile?> = userLocal.observeProfile()

    override suspend fun getProfile(): UserProfile? = userLocal.getProfile()

    override suspend fun saveProfile(
        name: String,
        birthDate: String,
        gender: Gender,
        birthTime: String?,
    ): Result<Unit> = runCatching {
        userLocal.saveProfile(name, birthDate, gender, birthTime)
    }

    override suspend fun updateNotifySettings(enabled: Boolean, time: String): Result<Unit> =
        runCatching { userLocal.updateNotifySettings(enabled, time) }

    override suspend fun clearAll(): Result<Unit> = runCatching { userLocal.clearAll() }
}
