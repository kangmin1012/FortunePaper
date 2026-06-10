# 아키텍처 규칙

> **v1.1 (2026-06-10)**: 카카오 로그인·서버 유저 DB 제거 → 로컬 프로필(DataStore) + stateless Edge Function + 로컬 알림. 배경·결정사항 → [`docs/spec-local-user-migration.md`](../../docs/spec-local-user-migration.md)

## 전체 전략

Google 권장 **Clean Architecture** + **MVI** 패턴 + **TOAD ViewModel** 패턴 조합.

```
Presentation Layer  →  Domain Layer  →  Data Layer
 (UI + ViewModel)      (UseCase)      (Repository 구현 + Local/Remote)
```

의존성은 항상 안쪽(Domain)을 향한다. Domain은 Presentation/Data를 모른다.

---

## 모듈 구조

```
FortuneReport/
├── androidApp/        # Android 진입점 — MainActivity만, 로직 없음
├── shared/            # 모든 UI + 비즈니스 로직 + 데이터 레이어
│   ├── commonMain/    # 플랫폼 독립 코드 전부 (Compose UI 포함)
│   ├── androidMain/   # Android expect/actual 구현체
│   └── iosMain/       # iOS expect/actual 구현체 + MainViewController
├── iosApp/            # iOS 진입점 — Xcode 프로젝트, 로직 없음
└── supabase/
    └── functions/
        └── fortune/   # stateless — 요청의 사주 정보로 Gemini 호출 → 리포트 JSON 반환 (DB 미사용)
```

---

## 레이어 구조 (shared/commonMain)

```
com.fortune.paper/
├── presentation/
│   ├── onboarding/                   # 7단계 온보딩 (Welcome "시작하기" → 입력 → 완료 시 로컬 저장)
│   ├── report/
│   │   ├── ReportScreen.kt           # Composable UI
│   │   ├── ReportViewModel.kt        # ToadViewModel 상속
│   │   ├── ReportState.kt            # ViewState
│   │   ├── ReportEvent.kt            # ViewEvent (일회성 사이드 이펙트)
│   │   ├── ReportDependencies.kt     # ActionDependencies
│   │   └── actions/
│   │       ├── LoadReport.kt         # ViewAction (1액션 1파일)
│   │       └── RefreshReport.kt
│   └── settings/
│       ├── SettingsScreen.kt
│       ├── SettingsViewModel.kt
│       ├── SettingsState.kt
│       ├── SettingsEvent.kt
│       ├── SettingsDependencies.kt
│       └── actions/
│           ├── LoadSettings.kt
│           ├── UpdateNotifyTime.kt
│           ├── ToggleNotify.kt
│           ├── SaveProfile.kt        # 사주 입력값 변경 시 운세 캐시 무효화 포함
│           └── ResetAppData.kt       # 로컬 데이터 전체 삭제 + 알림 취소
├── domain/
│   ├── model/
│   │   ├── FortuneReport.kt          # date, grade, summary, advice
│   │   └── UserProfile.kt            # name, birthDate, gender, birthTime?, notifyEnabled, notifyTime
│   ├── repository/
│   │   ├── FortuneRepository.kt      # interface — getTodayReport / refreshReport / invalidateCache
│   │   └── UserRepository.kt         # interface — observeProfile(Flow) / getProfile / saveProfile / updateNotifySettings / clearAll
│   └── usecase/
│       ├── GetTodayReportUseCase.kt
│       ├── RefreshReportUseCase.kt
│       ├── SaveProfileUseCase.kt
│       ├── UpdateNotifyTimeUseCase.kt
│       └── ResetAppDataUseCase.kt
├── data/
│   ├── repository/
│   │   ├── FortuneRepositoryImpl.kt  # 로컬 캐시(date 일치) 우선 → 미스 시 remote 호출 후 캐시 저장
│   │   └── UserRepositoryImpl.kt     # 전부 로컬(DataStore) 위임
│   ├── local/
│   │   ├── DataStoreFactory.kt       # expect — actual은 androidMain(Context)/iosMain(NSDocumentDirectory)
│   │   ├── UserLocalDataSource.kt    # 프로필 + 알림 설정 read/write, observeProfile(): Flow
│   │   └── FortuneLocalDataSource.kt # 운세 캐시 read/write/clear
│   └── remote/
│       ├── SupabaseClientProvider.kt # 싱글턴 — Functions 플러그인만 설치
│       └── FortuneRemoteDataSource.kt # functions.invoke("fortune")
└── platform/
    └── notification/
        └── LocalNotifier.kt          # expect — requestPermission / scheduleDaily / cancel
```

---

## MVI 패턴

| 요소 | 역할 | 구현 |
|------|------|------|
| **Model** | 불변 UI 상태 단일 진실 공급원 | `ViewState` data class |
| **View** | 상태를 렌더링 | Composable — 상태만 받아 그림, 로직 없음 |
| **Intent** | 사용자 액션 → 상태 변경 요청 | `ViewAction` → `dispatch()` 호출 |

---

## TOAD 패턴

**TOAD = Typed Object Action Dispatch**
ViewModel 비대화 문제를 해결. 새 기능은 새 Action 파일 추가로만 대응한다 (ViewModel 수정 없음).

### ViewState
```kotlin
data class ReportState(
    val isLoading: Boolean = false,
    val report: FortuneReport? = null,
    val error: String? = null
) : ViewState
```

### ViewEvent — 일회성 사이드 이펙트만
```kotlin
sealed interface ReportEvent : ViewEvent {
    data class ShowError(val message: String) : ReportEvent
    data object NavigateToSettings : ReportEvent
}
```

### ActionDependencies
```kotlin
class ReportDependencies(
    override val coroutineScope: CoroutineScope,
    val getTodayReport: GetTodayReportUseCase
) : ActionDependencies()
```

### ViewAction — 1파일 1책임
```kotlin
data object LoadReport : ReportAction() {
    override suspend fun execute(
        dependencies: ReportDependencies,
        scope: ActionScope<ReportState, ReportEvent>
    ) {
        scope.setState { copy(isLoading = true) }
        dependencies.getTodayReport()
            .onSuccess { report ->
                scope.setState { copy(isLoading = false, report = report) }
            }
            .onFailure { e ->
                scope.setState { copy(isLoading = false) }
                scope.sendEvent(ReportEvent.ShowError(e.message ?: "오류 발생"))
            }
    }
}
```

### ToadViewModel
```kotlin
class ReportViewModel(deps: ReportDependencies) :
    ToadViewModel<ReportState, ReportEvent>(
        initialState = ReportState(),
        dependencies = deps
    )
// 외부에 노출되는 함수는 dispatch() 하나
```

### UI에서 사용
```kotlin
@Composable
fun ReportScreen(viewModel: ReportViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.dispatch(LoadReport) }

    ReportContent(
        state = state,
        onRefresh = { viewModel.dispatch(RefreshReport) }
    )
}
```

---

## 핵심 규칙

### Clean Architecture
- Domain 레이어는 Supabase, DataStore 등 외부 의존성을 import하지 않는다.
- Repository interface는 Domain에, 구현체는 Data에 위치한다.
- UseCase는 단일 `invoke()` 연산자만 갖는다.
- 모든 데이터 접근(DataStore 읽기/쓰기, Edge Function 호출)은 Repository를 통해서만 수행 — DataSource·SupabaseClient를 Presentation/Domain에서 직접 사용 금지.
- 당일 운세 캐시가 존재하면 절대 Edge Function을 재호출하지 않는다 — 이 규칙은 `FortuneRepositoryImpl` 레벨에서 강제한다 (예외: 프로필의 사주 입력값 변경 시 `invalidateCache()`).

### TOAD
- ViewModel의 공개 API는 `dispatch(action)` 하나뿐이다.
- 새 기능 = 새 Action 파일 추가. 기존 Action 수정은 최소화.
- Action 하나는 정확히 하나의 책임만 갖는다.
- `ActionScope` 밖에서 상태를 직접 변경하지 않는다.
- `ViewEvent`는 네비게이션, 토스트 등 일회성 사이드 이펙트에만 사용한다.

### expect/actual
- 플랫폼별 SDK가 필요한 경우에만 사용.
- 새 expect 추가 시 반드시 `androidMain` + `iosMain` 양쪽에 actual 구현.

---

## 테스트

Action 단위를 독립적으로 테스트한다. ViewModel 인스턴스화 불필요.

```kotlin
@Test
fun `LoadReport 성공 시 state에 report가 설정된다`() = runTest {
    val fakeReport = FortuneReport(grade = "A", summary = "좋은 날", advice = "실행하세요")
    val mockUseCase = mockk<GetTodayReportUseCase>()
    coEvery { mockUseCase() } returns Result.success(fakeReport)

    val scope = FakeActionScope(ReportState())
    LoadReport.execute(ReportDependencies(this, mockUseCase), scope)

    assertEquals(fakeReport, scope.currentState.report)
}
```

---

## 앱 시작 분기 (App.kt)

Supabase 세션이 없으므로 **로컬 프로필 존재 여부**로 분기한다.

```
App() → userRepository.observeProfile(): Flow<UserProfile?>
  ├─ 미로드(초기)        → LoadingScreen
  ├─ 프로필 없음          → OnboardingScreen
  └─ 프로필 있음(필수 3종) → ReportScreen
```

설정의 "데이터 초기화" 시 Flow가 null을 방출하여 자동으로 온보딩으로 복귀한다.

---

## 백엔드 연동

### 리포트 생성 흐름 (v1.1 — 로컬 캐시 + stateless 서버)
```
앱 → GetTodayReportUseCase → FortuneRepository
  → 로컬 캐시(DataStore) 확인: cached.date == 오늘(KST)?
  → 일치: 캐시 반환 (Edge Function 호출 없음 — 절대 규칙)
  → 불일치/없음: 로컬 프로필 읽기
      → Edge Function `fortune` 호출 — body: { birth_date, gender, birth_time }
      → Gemini API → { date, grade, summary, advice }
      → DataStore 캐시 저장 후 반환
```

- Edge Function은 `verify_jwt = false`로 배포 (Supabase Auth 제거로 사용자 JWT 부재. 게이트웨이 apikey 검사는 유지).
- 서버는 요청을 저장하지 않는다 — DB·supabase-js 미사용.
- `birth_time`이 null이면 서버 프롬프트가 정오 대표값으로 처리.

### AI API 요청/응답 스펙
```json
// 요청
{ "birth_date": "1995-01-01", "gender": "MALE", "birth_time": "자" }
// 응답
{ "date": "2026-06-10", "grade": "SUNNY", "summary": "한 문장 (20자 이내)", "advice": "오늘의 조언 (50자 이내)" }
```
grade 허용값: `SUNNY | CLEAR | CLOUDY | RAINY | STORM` (날씨 5단계, 자세한 톤 가이드 → `prd.md` 5절)

> **현재 사용 AI**: Gemini API (Google AI Studio 무료 티어 — Gemini 1.5 Flash)
> 출시 전 Claude API 교체 검토. API 호출부는 Edge Function에 격리되어 있어 교체 시 앱 코드 변경 없음.

### 로컬 알림 흐름 (v1.1 — 서버 푸시 없음)
```
온보딩 완료 / 설정 변경
  → LocalNotifier (expect/actual)
      Android: AlarmManager.setAndAllowWhileIdle(inexact)
               + BroadcastReceiver에서 알림 표시 후 다음날 재예약
               + BOOT_COMPLETED 리시버로 재부팅 시 재예약
               + API 33+ POST_NOTIFICATIONS 런타임 권한
      iOS:     UNCalendarNotificationTrigger(hour/minute, repeats: true) 1회 등록
```
알림 문구는 고정 (발송 시점에 당일 운세 미생성이므로 등급 포함 불가).

---

## 로컬 데이터 스키마 (DataStore)

파일: `fortune_paper.preferences_pb` (androidx.datastore preferences, KMP)

| 키 | 타입 | 설명 |
|----|------|------|
| `profile_name` | String | 표시 이름 (필수, 최대 12자) |
| `profile_birth_date` | String | YYYY-MM-DD (필수) |
| `profile_gender` | String | `MALE` / `FEMALE` (필수) |
| `profile_birth_time` | String? | 12시진 (`자`~`해`), 미설정 시 없음 → 서버가 정오 대표값 처리 |
| `notify_enabled` | Boolean | 알림 토글 (기본 true) — 시각과 분리하여 끄기/켜기 시 시각 보존 |
| `notify_time` | String | HH:mm (기본 07:30) |
| `fortune_cache_json` | String | `FortuneReport` JSON — `date`(KST)가 오늘과 다르면 무효 |

온보딩 완료 판단: 별도 플래그 없이 필수 3종(`profile_name`·`profile_birth_date`·`profile_gender`) 존재 여부 (이중 상태 방지).

서버 DB 스키마 없음 — v1.0의 `users`·`fortunes` 테이블은 Task 8.6에서 DROP.

---

## 개발 순서

1. ~~프로젝트 세팅~~ ✅
2. ~~DB 스키마~~ ✅ (v1.1에서 폐기)
3. ~~카카오 로그인 + 온보딩~~ ✅ (로그인은 v1.1에서 제거)
4. ~~운세 리포트 생성 (Edge Function)~~ ✅ (v1.1에서 stateless 개편)
5. ~~리포트 화면 UI (TOAD + MVI)~~ ✅
6. **로컬 전환 마이그레이션 (Task 8)** — DataStore → 온보딩/App 분기 전환 → Edge Function 개편 → 리포트 흐름 전환 → 카카오 제거 → 설정 화면 → 로컬 알림
7. 배포 (Google Play / App Store)
