package com.fortune.paper.platform.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** 알람 수신 → 고정 문구 알림 표시 → 다음날 같은 시각으로 재예약. */
class DailyFortuneAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showDailyNotification(context)

        val hour = intent.getIntExtra(EXTRA_HOUR, 7)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 30)
        scheduleNextAlarm(context, hour, minute)
    }

    companion object {
        const val EXTRA_HOUR = "hour"
        const val EXTRA_MINUTE = "minute"
    }
}
