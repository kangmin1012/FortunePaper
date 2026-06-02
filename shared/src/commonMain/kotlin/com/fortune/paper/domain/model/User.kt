package com.fortune.paper.domain.model

data class User(
    val id: String,
    val kakaoId: String,
    val name: String,
    val birthDate: String,
    val gender: Gender,
    val birthTime: String?,
    val notifyTime: String?,
    val fcmToken: String?
)

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
