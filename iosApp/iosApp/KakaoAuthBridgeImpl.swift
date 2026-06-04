import Foundation
import Shared
import KakaoSDKAuth
import KakaoSDKUser

/// Kotlin `KakaoAuthBridge` 인터페이스의 Swift 구현체.
/// 카카오 iOS SDK(Swift)는 Kotlin/Native cinterop으로 직접 호출할 수 없으므로
/// 이 브리지를 앱 시작 시 Kotlin 쪽에 주입한다.
class KakaoAuthBridgeImpl: KakaoAuthBridge {

    func login(onSuccess: @escaping (String, KotlinLong) -> Void,
               onError: @escaping (String) -> Void) {

        // 토큰 수령 → 사용자 ID 조회 → onSuccess
        let handleToken: (OAuthToken?, Error?) -> Void = { token, error in
            if let error = error {
                onError(error.localizedDescription)
                return
            }
            guard let accessToken = token?.accessToken else {
                onError("카카오 토큰이 없습니다")
                return
            }
            UserApi.shared.me { user, meError in
                if let meError = meError {
                    onError(meError.localizedDescription)
                    return
                }
                guard let id = user?.id else {
                    onError("사용자 ID를 가져올 수 없습니다")
                    return
                }
                onSuccess(accessToken, KotlinLong(value: id))
            }
        }

        if UserApi.isKakaoTalkLoginAvailable() {
            UserApi.shared.loginWithKakaoTalk { token, error in
                if error != nil {
                    // 카카오톡 로그인 실패 시 카카오계정 로그인으로 폴백
                    UserApi.shared.loginWithKakaoAccount(completion: handleToken)
                } else {
                    handleToken(token, nil)
                }
            }
        } else {
            UserApi.shared.loginWithKakaoAccount(completion: handleToken)
        }
    }

    func logout(onComplete: @escaping () -> Void) {
        UserApi.shared.logout { _ in onComplete() }
    }
}
