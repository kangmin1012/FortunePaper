package com.fortune.paper.di

import com.fortune.paper.auth.KakaoAuth
import org.koin.dsl.module

val androidAuthModule = module {
    single<KakaoAuth> { KakaoAuth() }
}
