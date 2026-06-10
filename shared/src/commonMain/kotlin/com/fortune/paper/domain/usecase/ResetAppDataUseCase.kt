package com.fortune.paper.domain.usecase

import com.fortune.paper.domain.repository.FortuneRepository
import com.fortune.paper.domain.repository.UserRepository

/** 정보 초기화 — 로컬 데이터 전체 삭제. (알림 취소는 호출부에서 LocalNotifier로 수행) */
class ResetAppDataUseCase(
    private val userRepository: UserRepository,
    private val fortuneRepository: FortuneRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        fortuneRepository.invalidateCache()
        userRepository.clearAll().getOrThrow()
    }
}
