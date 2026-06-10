package com.fortune.paper

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.fortune.paper.platform.notification.NotificationPermissionRequester
import kotlinx.coroutines.CompletableDeferred

class MainActivity : ComponentActivity() {

    private var permissionResult: CompletableDeferred<Boolean>? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionResult?.complete(granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 공유 모듈(LocalNotifier)이 POST_NOTIFICATIONS 런타임 권한을 요청할 수 있게 브리지 등록
        NotificationPermissionRequester.request = {
            val deferred = CompletableDeferred<Boolean>()
            permissionResult = deferred
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            deferred.await()
        }

        setContent { App() }
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationPermissionRequester.request = null
    }
}
