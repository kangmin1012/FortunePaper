package com.fortune.paper.domain.usecase

import com.fortune.paper.domain.repository.UserRepository

/** 알림 설정 저장 — enabled(토글)와 time(HH:mm)을 분리 저장해 끄더라도 시각이 보존된다. */
class UpdateNotifyTimeUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(enabled: Boolean, time: String): Result<Unit> =
        repository.updateNotifySettings(enabled, time)
}
