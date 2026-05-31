package com.fortune.paper.data.repository

import com.fortune.paper.data.remote.UserDto
import com.fortune.paper.data.remote.UserRemoteDataSource
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.domain.model.User
import com.fortune.paper.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userRemote: UserRemoteDataSource
) : UserRepository {

    override suspend fun loginWithKakao(kakaoAccessToken: String): Result<Boolean> = runCatching {
        userRemote.signInWithKakao(kakaoAccessToken)
        val userId = requireNotNull(userRemote.currentUserId()) { "인증에 실패했습니다" }
        val profile = userRemote.getUser(userId)
        profile == null // true = 온보딩 필요
    }

    override fun isLoggedIn(): Boolean = userRemote.isLoggedIn()

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        val userId = requireNotNull(userRemote.currentUserId()) { "로그인이 필요합니다" }
        val dto = requireNotNull(userRemote.getUser(userId)) { "사용자 정보를 찾을 수 없습니다" }
        dto.toDomain()
    }

    override suspend fun saveUser(kakaoId: String, birthDate: String, gender: String): Result<User> =
        runCatching { userRemote.upsertUser(kakaoId, birthDate, gender).toDomain() }

    override suspend fun updateNotifyTime(time: String?): Result<Unit> = runCatching {
        val userId = requireNotNull(userRemote.currentUserId()) { "로그인이 필요합니다" }
        userRemote.updateNotifyTime(userId, time)
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> = runCatching {
        val userId = requireNotNull(userRemote.currentUserId()) { "로그인이 필요합니다" }
        userRemote.updateFcmToken(userId, token)
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        userRemote.signOut()
    }

    private fun UserDto.toDomain() = User(
        id = id,
        kakaoId = kakao_id,
        birthDate = birth_date,
        gender = Gender.fromString(gender),
        notifyTime = notify_time,
        fcmToken = fcm_token
    )
}
