import SwiftUI
import Shared
import KakaoSDKCommon
import KakaoSDKAuth

@main
struct iOSApp: App {

    init() {
        // 설정값은 Info.plist(← Config/Secrets.xcconfig)에서 읽는다.
        let info = Bundle.main.infoDictionary
        let kakaoAppKey = info?["KAKAO_NATIVE_APP_KEY"] as? String ?? ""
        let supabaseUrl = info?["SUPABASE_URL"] as? String ?? ""
        let supabaseAnonKey = info?["SUPABASE_ANON_KEY"] as? String ?? ""

        // 카카오 SDK 초기화
        KakaoSDK.initSDK(appKey: kakaoAppKey)

        // 공유 모듈 부트스트랩(Koin + Supabase + Kakao 브리지 주입)
        IosAppKt.startApp(
            supabaseUrl: supabaseUrl,
            supabaseAnonKey: supabaseAnonKey,
            kakaoBridge: KakaoAuthBridgeImpl()
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // 카카오톡 앱으로 로그인 후 돌아오는 콜백 처리
                    if AuthApi.isKakaoTalkLoginUrl(url) {
                        _ = AuthController.handleOpenUrl(url: url)
                    }
                }
        }
    }
}
