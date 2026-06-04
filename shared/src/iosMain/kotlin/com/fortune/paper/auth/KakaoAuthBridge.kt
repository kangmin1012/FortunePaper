package com.fortune.paper.auth

/**
 * iOS 카카오 SDK는 Swift로 작성되어 Kotlin/Native cinterop으로 직접 호출할 수 없다.
 * 따라서 Swift가 이 인터페이스를 구현하고, 앱 시작 시 [KakaoBridgeHolder]에 주입한다.
 */
interface KakaoAuthBridge {
    fun login(
        onSuccess: (accessToken: String, userId: Long) -> Unit,
        onError: (message: String) -> Unit
    )

    fun logout(onComplete: () -> Unit)
}

object KakaoBridgeHolder {
    var bridge: KakaoAuthBridge? = null
}
