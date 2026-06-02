package com.fortune.paper.di

import com.fortune.paper.data.remote.FortuneRemoteDataSource
import com.fortune.paper.data.remote.SupabaseClientProvider
import com.fortune.paper.data.remote.UserRemoteDataSource
import com.fortune.paper.data.repository.FortuneRepositoryImpl
import com.fortune.paper.data.repository.UserRepositoryImpl
import com.fortune.paper.domain.repository.FortuneRepository
import com.fortune.paper.domain.repository.UserRepository
import com.fortune.paper.domain.usecase.GetTodayReportUseCase
import com.fortune.paper.domain.usecase.RefreshReportUseCase
import com.fortune.paper.domain.usecase.UpdateNotifyTimeUseCase
import com.fortune.paper.presentation.login.LoginDependencies
import com.fortune.paper.presentation.login.LoginViewModel
import com.fortune.paper.presentation.onboarding.OnboardingDependencies
import com.fortune.paper.presentation.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataModule = module {
    single { SupabaseClientProvider.client }
    single { FortuneRemoteDataSource(get()) }
    single { UserRemoteDataSource(get()) }
    single<FortuneRepository> { FortuneRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}

val domainModule = module {
    factoryOf(::GetTodayReportUseCase)
    factoryOf(::RefreshReportUseCase)
    factoryOf(::UpdateNotifyTimeUseCase)
}

val presentationModule = module {
    factory { LoginViewModel(LoginDependencies(userRepository = get(), kakaoAuth = get())) }
    factory { OnboardingViewModel(OnboardingDependencies(userRepository = get(), kakaoAuth = get())) }
}

val appModules = listOf(dataModule, domainModule, presentationModule)
