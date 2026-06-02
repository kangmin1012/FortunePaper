package com.fortune.paper.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.call.body
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class UserDto(
    val id: String,
    val kakao_id: String,
    val name: String,
    val birth_date: String,
    val gender: String,
    val birth_time: String? = null,
    val notify_time: String? = null,
    val fcm_token: String? = null,
    val created_at: String
)

@Serializable
data class UserUpsert(
    val id: String,
    val kakao_id: String,
    val name: String,
    val birth_date: String,
    val gender: String,
    val birth_time: String? = null
)

@Serializable
private data class KakaoAuthResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Long,
    val kakao_id: String
)

class UserRemoteDataSource(private val client: SupabaseClient) {

    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    fun isLoggedIn(): Boolean = client.auth.currentSessionOrNull() != null

    suspend fun signInWithKakao(kakaoAccessToken: String) {
        val response = client.functions.invoke(
            function = "kakao-auth",
            body = buildJsonObject { put("access_token", kakaoAccessToken) }
        )
        val authData = response.body<KakaoAuthResponse>()

        client.auth.importSession(
            UserSession(
                accessToken = authData.access_token,
                refreshToken = authData.refresh_token,
                expiresIn = authData.expires_in,
                tokenType = "bearer"
            )
        )
        client.auth.retrieveUserForCurrentSession(updateSession = true)
    }

    suspend fun getUser(userId: String): UserDto? {
        return client.postgrest["users"]
            .select(Columns.ALL) {
                filter { eq("id", userId) }
            }
            .decodeSingleOrNull<UserDto>()
    }

    suspend fun upsertUser(
        kakaoId: String,
        name: String,
        birthDate: String,
        gender: String,
        birthTime: String?
    ): UserDto {
        val userId = requireNotNull(currentUserId()) { "인증된 사용자가 없음" }
        client.postgrest["users"].upsert(
            UserUpsert(
                id = userId,
                kakao_id = kakaoId,
                name = name,
                birth_date = birthDate,
                gender = gender,
                birth_time = birthTime
            )
        )
        return requireNotNull(getUser(userId))
    }

    suspend fun updateNotifyTime(userId: String, time: String?) {
        client.postgrest["users"].update(
            { set("notify_time", time) }
        ) {
            filter { eq("id", userId) }
        }
    }

    suspend fun updateFcmToken(userId: String, token: String) {
        client.postgrest["users"].update(
            { set("fcm_token", token) }
        ) {
            filter { eq("id", userId) }
        }
    }

    suspend fun signOut() {
        client.auth.signOut()
    }
}
