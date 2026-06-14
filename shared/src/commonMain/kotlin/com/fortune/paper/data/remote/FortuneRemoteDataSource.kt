package com.fortune.paper.data.remote

import com.fortune.paper.domain.model.FortuneRateLimitedException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/** fortune Edge Function 응답 (v1.1 stateless 스펙) */
@Serializable
data class FortuneDto(
    val date: String,
    val grade: String,
    val summary: String,
    val advice: String,
)

class FortuneRemoteDataSource(private val client: SupabaseClient) {

    suspend fun generateReport(
        birthDate: String,
        gender: String,
        birthTime: String?,
    ): FortuneDto {
        val response = client.functions.invoke(
            function = "fortune",
            body = buildJsonObject {
                put("birth_date", birthDate)
                put("gender", gender)
                if (birthTime != null) put("birth_time", birthTime)
                else put("birth_time", JsonNull)
            },
        )
        // 무료 티어 한도 초과 → 전용 예외로 변환 (리포트 화면이 "용지 소진" 다이얼로그로 안내).
        if (response.status.value == 429) throw FortuneRateLimitedException()
        return response.body<FortuneDto>()
    }
}
