package com.fortune.paper.auth

// iOS Kakao SDK 연동은 Xcode SPM 패키지 추가 후 구현합니다.
// SPM: https://github.com/kakao/kakao-ios-sdk (KakaoSDKUser 추가)
actual class KakaoAuth {
    actual suspend fun login(): Result<KakaoToken> =
        Result.failure(NotImplementedError("iOS Kakao SDK 미구현 (Xcode SPM 설정 필요)"))

    actual suspend fun logout() {}
}
