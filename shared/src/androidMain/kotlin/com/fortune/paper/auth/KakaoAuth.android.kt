package com.fortune.paper.auth

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual class KakaoAuth {

    actual suspend fun login(): Result<KakaoToken> {
        val activity = KakaoAuthHolder.activity
            ?: return Result.failure(IllegalStateException("Activity를 찾을 수 없습니다"))

        return runCatching {
            val oauthToken = loginWithKakao(activity)
            val userId = getKakaoUserId()
            KakaoToken(accessToken = oauthToken.accessToken, userId = userId)
        }
    }

    private suspend fun loginWithKakao(
        activity: android.app.Activity
    ): OAuthToken = suspendCoroutine { cont ->
        val fallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            when {
                error != null -> cont.resumeWithException(error)
                token != null -> cont.resume(token)
                else -> cont.resumeWithException(IllegalStateException("토큰이 없습니다"))
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
                when {
                    error is ClientError && error.reason == ClientErrorCause.Cancelled ->
                        cont.resumeWithException(error)
                    error != null ->
                        UserApiClient.instance.loginWithKakaoAccount(activity, callback = fallback)
                    token != null -> cont.resume(token)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity, callback = fallback)
        }
    }

    private suspend fun getKakaoUserId(): Long = suspendCoroutine { cont ->
        UserApiClient.instance.me { user, error ->
            when {
                error != null -> cont.resumeWithException(error)
                user?.id != null -> cont.resume(user.id!!)
                else -> cont.resumeWithException(IllegalStateException("사용자 ID를 가져올 수 없습니다"))
            }
        }
    }

    actual suspend fun logout() {
        suspendCoroutine<Unit> { cont ->
            UserApiClient.instance.logout { _ -> cont.resume(Unit) }
        }
    }
}
