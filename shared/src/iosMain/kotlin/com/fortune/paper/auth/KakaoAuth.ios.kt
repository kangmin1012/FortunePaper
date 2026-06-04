package com.fortune.paper.auth

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual class KakaoAuth {

    actual suspend fun login(): Result<KakaoToken> {
        val bridge = KakaoBridgeHolder.bridge
            ?: return Result.failure(IllegalStateException("Kakao 브리지가 설정되지 않았습니다"))

        return runCatching {
            suspendCoroutine { cont ->
                bridge.login(
                    onSuccess = { accessToken, userId ->
                        cont.resume(KakaoToken(accessToken = accessToken, userId = userId))
                    },
                    onError = { message ->
                        cont.resumeWithException(IllegalStateException(message))
                    }
                )
            }
        }
    }

    actual suspend fun logout() {
        val bridge = KakaoBridgeHolder.bridge ?: return
        suspendCoroutine<Unit> { cont ->
            bridge.logout { cont.resume(Unit) }
        }
    }
}
