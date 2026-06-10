package com.fortune.paper.platform.notification

/** 알림 고정 문구 — 발송 시점에 당일 운세가 아직 생성 전이므로 등급을 포함하지 않는다 (PRD §4.4). */
const val NOTIFICATION_TITLE = "포춘페이퍼"
const val NOTIFICATION_BODY = "오늘의 포춘페이퍼가 도착했어요 📰"

/**
 * 매일 반복 로컬 알림.
 * - Android: AlarmManager.setAndAllowWhileIdle(inexact) + 발송 후 재예약 + 재부팅 재예약
 * - iOS: UNCalendarNotificationTrigger(repeats = true)
 */
expect class LocalNotifier {
    /** 알림 권한 요청. 거부되어도 호출부 흐름은 계속된다. */
    suspend fun requestPermission(): Boolean
    fun scheduleDaily(hour: Int, minute: Int)
    fun cancel()
}

/** "HH:mm" → (hour, minute). 형식이 깨졌으면 기본 07:30. */
fun parseNotifyTime(time: String): Pair<Int, Int> {
    val parts = time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull()?.takeIf { it in 0..23 } ?: 7
    val minute = parts.getOrNull(1)?.toIntOrNull()?.takeIf { it in 0..59 } ?: 30
    return hour to minute
}
