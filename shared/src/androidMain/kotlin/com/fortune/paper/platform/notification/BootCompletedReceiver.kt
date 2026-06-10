package com.fortune.paper.platform.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fortune.paper.data.local.UserLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

/** 재부팅 시 알람이 모두 사라지므로 저장된 알림 설정으로 재예약한다. */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        // 리시버 기동 시 Application.onCreate(Koin 시작)는 이미 수행된 상태다.
        val koin = GlobalContext.getOrNull() ?: return
        val userLocal = koin.get<UserLocalDataSource>()

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val profile = userLocal.getProfile()
                if (profile != null && profile.notifyEnabled) {
                    val (hour, minute) = parseNotifyTime(profile.notifyTime)
                    LocalNotifier(context.applicationContext).scheduleDaily(hour, minute)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
