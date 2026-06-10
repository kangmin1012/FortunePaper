package com.fortune.paper.platform.notification

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import java.util.Calendar

internal const val NOTIFY_CHANNEL_ID = "fortune_daily"
internal const val ALARM_REQUEST_CODE = 1001
internal const val NOTIFICATION_ID = 1

/**
 * inexact alarm 채택 — 수 분 오차 수용, 특수 권한 불필요 (spec §4.2).
 * 발송 후 재예약은 [DailyFortuneAlarmReceiver], 재부팅 재예약은 [BootCompletedReceiver].
 */
actual class LocalNotifier(private val context: Context) {

    actual suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < 33) return true
        val granted = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) return true
        return NotificationPermissionRequester.request?.invoke() ?: false
    }

    actual fun scheduleDaily(hour: Int, minute: Int) {
        ensureNotificationChannel(context)
        scheduleNextAlarm(context, hour, minute)
    }

    actual fun cancel() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmPendingIntent(context, 0, 0))
    }
}

/** MainActivity가 등록하는 POST_NOTIFICATIONS 런타임 권한 요청 브리지 (API 33+). */
object NotificationPermissionRequester {
    var request: (suspend () -> Boolean)? = null
}

internal fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= 26) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                NOTIFY_CHANNEL_ID,
                "오늘의 리포트 알림",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        )
    }
}

/** 다음 hour:minute 발생 시각(이미 지났으면 내일)에 inexact 알람 1회 예약. */
internal fun scheduleNextAlarm(context: Context, hour: Int, minute: Int) {
    val next = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
    }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        next.timeInMillis,
        alarmPendingIntent(context, hour, minute),
    )
}

// PendingIntent 매칭은 extras를 무시하므로 cancel 시 hour/minute 값과 무관하게 동일 알람이 취소된다.
internal fun alarmPendingIntent(context: Context, hour: Int, minute: Int): PendingIntent {
    val intent = Intent(context, DailyFortuneAlarmReceiver::class.java).apply {
        putExtra(DailyFortuneAlarmReceiver.EXTRA_HOUR, hour)
        putExtra(DailyFortuneAlarmReceiver.EXTRA_MINUTE, minute)
    }
    return PendingIntent.getBroadcast(
        context,
        ALARM_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}

internal fun showDailyNotification(context: Context) {
    val manager = context.getSystemService(NotificationManager::class.java)
    if (!manager.areNotificationsEnabled()) return
    ensureNotificationChannel(context)

    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    val contentIntent = launchIntent?.let {
        PendingIntent.getActivity(
            context, 0, it,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    val builder = if (Build.VERSION.SDK_INT >= 26) {
        Notification.Builder(context, NOTIFY_CHANNEL_ID)
    } else {
        @Suppress("DEPRECATION")
        Notification.Builder(context)
    }
    val notification = builder
        .setSmallIcon(context.applicationInfo.icon)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(NOTIFICATION_BODY)
        .setContentIntent(contentIntent)
        .setAutoCancel(true)
        .build()

    manager.notify(NOTIFICATION_ID, notification)
}
