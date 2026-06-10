package com.fortune.paper.domain.usecase

import com.fortune.paper.domain.model.Gender
import com.fortune.paper.domain.repository.FortuneRepository
import com.fortune.paper.domain.repository.UserRepository

/**
 * 프로필 저장. 사주 입력값(생년월일·성별·시진)이 변경된 경우에만
 * 당일 운세 캐시를 무효화한다 (이름만 변경 시 캐시 유지 — PRD §4.3).
 */
class SaveProfileUseCase(
    private val userRepository: UserRepository,
    private val fortuneRepository: FortuneRepository,
) {
    suspend operator fun invoke(
        name: String,
        birthDate: String,
        gender: Gender,
        birthTime: String?,
    ): Result<Unit> {
        val old = userRepository.getProfile()
        return userRepository.saveProfile(name, birthDate, gender, birthTime).onSuccess {
            val sajuChanged = old == null ||
                old.birthDate != birthDate ||
                old.gender != gender ||
                old.birthTime != birthTime
            if (sajuChanged) fortuneRepository.invalidateCache()
        }
    }
}
