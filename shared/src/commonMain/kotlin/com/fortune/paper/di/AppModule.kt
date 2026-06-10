package com.fortune.paper.di

import com.fortune.paper.data.local.FortuneLocalDataSource
import com.fortune.paper.data.local.UserLocalDataSource
import com.fortune.paper.data.remote.FortuneRemoteDataSource
import com.fortune.paper.data.remote.SupabaseClientProvider
import com.fortune.paper.data.repository.FortuneRepositoryImpl
import com.fortune.paper.data.repository.UserRepositoryImpl
import com.fortune.paper.domain.repository.FortuneRepository
import com.fortune.paper.domain.repository.UserRepository
import com.fortune.paper.domain.usecase.GetTodayReportUseCase
import com.fortune.paper.domain.usecase.RefreshReportUseCase
import com.fortune.paper.domain.usecase.ResetAppDataUseCase
import com.fortune.paper.domain.usecase.SaveProfileUseCase
import com.fortune.paper.domain.usecase.UpdateNotifyTimeUseCase
import com.fortune.paper.presentation.onboarding.OnboardingDependencies
import com.fortune.paper.presentation.onboarding.OnboardingViewModel
import com.fortune.paper.presentation.report.ReportDependencies
import com.fortune.paper.presentation.report.ReportViewModel
import com.fortune.paper.presentation.settings.SettingsDependencies
import com.fortune.paper.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataModule = module {
    single { SupabaseClientProvider.client }
    single { FortuneRemoteDataSource(get()) }
    single { UserLocalDataSource(get()) }      // get() = DataStore<Preferences> (platformModule 제공)
    single { FortuneLocalDataSource(get()) }
    single<FortuneRepository> { FortuneRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}

val domainModule = module {
    factoryOf(::GetTodayReportUseCase)
    factoryOf(::RefreshReportUseCase)
    factoryOf(::SaveProfileUseCase)
    factoryOf(::UpdateNotifyTimeUseCase)
    factoryOf(::ResetAppDataUseCase)
}

val presentationModule = module {
    factory {
        OnboardingViewModel(
            OnboardingDependencies(
                saveProfile = get(),
                updateNotifySettings = get(),
                notifier = get(),
            )
        )
    }
    factory { ReportViewModel(ReportDependencies(getTodayReport = get(), refreshReport = get())) }
    factory {
        SettingsViewModel(
            SettingsDependencies(
                userRepository = get(),
                saveProfile = get(),
                updateNotifySettings = get(),
                resetAppData = get(),
                notifier = get(),
            )
        )
    }
}

val appModules = listOf(dataModule, domainModule, presentationModule)
