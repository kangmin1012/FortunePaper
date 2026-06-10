package com.fortune.paper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.fortune.paper.data.local.createDataStore
import com.fortune.paper.data.remote.SupabaseClientProvider
import com.fortune.paper.di.appModules
import com.fortune.paper.platform.notification.LocalNotifier
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val platformModule = module {
    single<DataStore<Preferences>> { createDataStore() }
    single { LocalNotifier() }
}

/**
 * iOS 앱 부트스트랩. Swift의 `iOSApp.init()` 에서 정확히 1회 호출한다.
 * (Android의 [com.fortune.paper.FortuneApp] 와 동일한 역할)
 *
 * 함수명을 `init*` 으로 시작하면 ObjC 인터롭에서 이니셜라이저로 취급되어
 * Swift 멤버로 노출되지 않으므로 `startApp` 으로 둔다.
 */
fun startApp(
    supabaseUrl: String,
    supabaseAnonKey: String,
) {
    SupabaseClientProvider.initialize(url = supabaseUrl, anonKey = supabaseAnonKey)
    startKoin {
        modules(appModules + platformModule)
    }
}
