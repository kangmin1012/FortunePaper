package com.fortune.paper.data.remote

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
        return response.body<FortuneDto>()
    }
}
