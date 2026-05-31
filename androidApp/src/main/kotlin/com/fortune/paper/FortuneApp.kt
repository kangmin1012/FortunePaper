package com.fortune.paper

import android.app.Application
import com.fortune.paper.data.remote.SupabaseClientProvider
import com.fortune.paper.di.androidAuthModule
import com.fortune.paper.di.appModules
import com.kakao.sdk.common.KakaoSdk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FortuneApp : Application() {
    override fun onCreate() {
        super.onCreate()

        SupabaseClientProvider.initialize(
            url = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY
        )

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        startKoin {
            androidContext(this@FortuneApp)
            modules(appModules + androidAuthModule)
        }
    }
}
