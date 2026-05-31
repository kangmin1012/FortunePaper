package com.fortune.paper.domain.repository

import com.fortune.paper.domain.model.User

interface UserRepository {
    suspend fun loginWithKakao(kakaoAccessToken: String): Result<Boolean>
    fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): Result<User>
    suspend fun saveUser(kakaoId: String, birthDate: String, gender: String): Result<User>
    suspend fun updateNotifyTime(time: String?): Result<Unit>
    suspend fun updateFcmToken(token: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
}
