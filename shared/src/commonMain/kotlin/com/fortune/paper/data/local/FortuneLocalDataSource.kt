package com.fortune.paper.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** 당일 운세 캐시 — `fortune_cache_json` 키에 JSON 직렬화 저장. date(KST) 불일치 시 무효. */
@Serializable
data class FortuneCache(
    val date: String,
    val grade: String,
    val summary: String,
    val advice: String,
)

class FortuneLocalDataSource(private val dataStore: DataStore<Preferences>) {

    private val key = stringPreferencesKey("fortune_cache_json")
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCache(): FortuneCache? =
        dataStore.data.first()[key]?.let { raw ->
            runCatching { json.decodeFromString<FortuneCache>(raw) }.getOrNull()
        }

    suspend fun saveCache(cache: FortuneCache) {
        dataStore.edit { it[key] = json.encodeToString(FortuneCache.serializer(), cache) }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(key) }
    }
}
