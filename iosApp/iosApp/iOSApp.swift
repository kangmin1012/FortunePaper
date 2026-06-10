import SwiftUI
import Shared

@main
struct iOSApp: App {

    init() {
        // 설정값은 Info.plist(← Config/Secrets.xcconfig)에서 읽는다.
        let info = Bundle.main.infoDictionary
        let supabaseUrl = info?["SUPABASE_URL"] as? String ?? ""
        let supabaseAnonKey = info?["SUPABASE_ANON_KEY"] as? String ?? ""

        // 공유 모듈 부트스트랩(Koin + Supabase Functions)
        IosAppKt.startApp(
            supabaseUrl: supabaseUrl,
            supabaseAnonKey: supabaseAnonKey
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
