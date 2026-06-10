package com.fortune.paper.data.repository

import com.fortune.paper.core.util.getTodayDateString
import com.fortune.paper.data.local.FortuneCache
import com.fortune.paper.data.local.FortuneLocalDataSource
import com.fortune.paper.data.local.UserLocalDataSource
import com.fortune.paper.data.remote.FortuneRemoteDataSource
import com.fortune.paper.domain.model.FortuneGrade
import com.fortune.paper.domain.model.FortuneReport
import com.fortune.paper.domain.repository.FortuneRepository

/**
 * v1.1 — 로컬 캐시 우선.
 * 당일(KST) 캐시가 존재하면 절대 Edge Function을 재호출하지 않는다 (절대 규칙).
 * 예외는 프로필의 사주 입력값 변경 시 [invalidateCache] 뿐이다.
 */
class FortuneRepositoryImpl(
    private val fortuneRemote: FortuneRemoteDataSource,
    private val fortuneLocal: FortuneLocalDataSource,
    private val userLocal: UserLocalDataSource,
) : FortuneRepository {

    override suspend fun getTodayReport(): Result<FortuneReport> = runCatching { loadReport() }

    override suspend fun refreshReport(): Result<FortuneReport> = runCatching { loadReport() }

    override suspend fun invalidateCache() {
        fortuneLocal.clear()
    }

    private suspend fun loadReport(): FortuneReport {
        val today = getTodayDateString()
        fortuneLocal.getCache()
            ?.takeIf { it.date == today }
            ?.let { return it.toDomain() }

        val profile = requireNotNull(userLocal.getProfile()) { "프로필 정보가 없습니다" }
        val dto = fortuneRemote.generateReport(
            birthDate = profile.birthDate,
            gender = profile.gender.name,
            birthTime = profile.birthTime,
        )
        val cache = FortuneCache(
            date = dto.date.ifBlank { today },
            grade = dto.grade,
            summary = dto.summary,
            advice = dto.advice,
        )
        fortuneLocal.saveCache(cache)
        return cache.toDomain()
    }

    private fun FortuneCache.toDomain() = FortuneReport(
        date = date,
        grade = FortuneGrade.fromString(grade),
        summary = summary,
        advice = advice,
    )
}
