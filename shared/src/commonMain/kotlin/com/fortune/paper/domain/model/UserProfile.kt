package com.fortune.paper.domain.model

/**
 * 기기 로컬(DataStore)에 저장되는 유저 프로필 (v1.1 — 계정 없음).
 * 필수 3종(name·birthDate·gender)의 존재 여부가 온보딩 완료 판단 기준이다.
 */
data class UserProfile(
    val name: String,
    /** YYYY-MM-DD */
    val birthDate: String,
    val gender: Gender,
    /** 태어난 시각 12시진(자~해). null이면 미설정 → 서버가 정오 대표값 처리 */
    val birthTime: String?,
    val notifyEnabled: Boolean = true,
    /** HH:mm */
    val notifyTime: String = DEFAULT_NOTIFY_TIME,
) {
    companion object {
        const val DEFAULT_NOTIFY_TIME = "07:30"
    }
}

enum class Gender {
    MALE, FEMALE;

    val displayName: String get() = when (this) {
        MALE -> "남"
        FEMALE -> "여"
    }

    companion object {
        fun fromString(value: String): Gender =
            entries.find { it.name == value.uppercase() } ?: MALE
    }
}
