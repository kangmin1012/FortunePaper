package com.fortune.paper.domain.repository

import com.fortune.paper.domain.model.Gender
import com.fortune.paper.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /** 필수 3종(이름·생년월일·성별)이 없으면 null — 앱 시작 분기·온보딩 완료 판단 기준 */
    fun observeProfile(): Flow<UserProfile?>
    suspend fun getProfile(): UserProfile?
    suspend fun saveProfile(
        name: String,
        birthDate: String,
        gender: Gender,
        birthTime: String?,
    ): Result<Unit>
    suspend fun updateNotifySettings(enabled: Boolean, time: String): Result<Unit>
    /** 로컬 데이터 전체 삭제 (정보 초기화) */
    suspend fun clearAll(): Result<Unit>
}
