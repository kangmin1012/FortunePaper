package com.fortune.paper.domain.repository

import com.fortune.paper.domain.model.FortuneReport

interface FortuneRepository {
    suspend fun getTodayReport(): Result<FortuneReport>
    /** 수동 새로고침 — 당일 캐시가 있으면 그대로 반환 (재생성 없음) */
    suspend fun refreshReport(): Result<FortuneReport>
    /** 사주 입력값(생년월일·성별·시진) 변경 시 당일 캐시 무효화 */
    suspend fun invalidateCache()
}
