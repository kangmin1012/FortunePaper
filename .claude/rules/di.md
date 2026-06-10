# DI 규칙 (Koin)

의존성 주입은 **Koin** 사용. 수동 생성자 주입 금지.

## 모듈 구성

모듈 정의는 `shared/commonMain`에 작성. 플랫폼별 모듈만 `androidMain`/`iosMain`에 추가.

```kotlin
val dataModule = module {
    single { SupabaseClientProvider.client }
    single { FortuneRemoteDataSource(get()) }
    single { UserLocalDataSource(get()) }      // get() = DataStore<Preferences> (platformModule 제공)
    single { FortuneLocalDataSource(get()) }
    single<FortuneRepository> { FortuneRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}

val domainModule = module {
    factory { GetTodayReportUseCase(get()) }
    factory { RefreshReportUseCase(get()) }
    factory { SaveProfileUseCase(get(), get()) }
    factory { UpdateNotifyTimeUseCase(get()) }
    factory { ResetAppDataUseCase(get(), get()) }
}

val presentationModule = module {
    factory { OnboardingViewModel(OnboardingDependencies(get(), get())) }
    factory { ReportViewModel(ReportDependencies(get(), get())) }
    factory { SettingsViewModel(SettingsDependencies(get(), get(), get())) }
}
```

플랫폼별 모듈 — DataStore 생성과 LocalNotifier처럼 플랫폼 API가 필요한 의존성만 제공한다.

```kotlin
// androidMain
val platformModule = module {
    single<DataStore<Preferences>> { createDataStore(androidContext()) }
    single<LocalNotifier> { LocalNotifier(androidContext()) }
}

// iosMain (IosApp.kt)
val platformModule = module {
    single<DataStore<Preferences>> { createDataStore() }
    single<LocalNotifier> { LocalNotifier() }
}
```

## 규칙

- ViewModel은 `koinViewModel()` 또는 `koinNavViewModel()`로 주입. Composable 내부에서 직접 생성 금지.
- UseCase는 `factory` (매번 새 인스턴스), Repository/Client는 `single` (싱글턴).
- `ActionDependencies`에 필요한 UseCase/Repository는 Koin 모듈에서 제공.
- Domain 레이어 클래스는 Koin에 의존하지 않는다 (순수 Kotlin).
