# 아키텍처 규칙

## 전체 전략

Google 권장 **Clean Architecture** + **MVI** 패턴 + **TOAD ViewModel** 패턴 조합.

```
Presentation Layer  →  Domain Layer  →  Data Layer
 (UI + ViewModel)      (UseCase)      (Repository 구현 + Remote)
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
        ├── fortune/   # Claude API 호출 → 리포트 JSON 반환
        └── notify/    # FCM 발송 트리거
```

---

## 레이어 구조 (shared/commonMain)

```
com.fortune.paper/
├── presentation/
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
│           └── UpdateNotifyTime.kt
├── domain/
│   ├── model/
│   │   ├── FortuneReport.kt          # grade, summary, advice
│   │   └── User.kt
│   ├── repository/
│   │   ├── FortuneRepository.kt      # interface
│   │   └── UserRepository.kt        # interface
│   └── usecase/
│       ├── GetTodayReportUseCase.kt
│       └── UpdateNotifyTimeUseCase.kt
├── data/
│   ├── repository/
│   │   ├── FortuneRepositoryImpl.kt
│   │   └── UserRepositoryImpl.kt
│   └── remote/
│       ├── SupabaseClient.kt         # 싱글턴, 직접 쿼리 금지
│       ├── FortuneRemoteDataSource.kt
│       └── UserRemoteDataSource.kt
└── auth/
    └── KakaoAuth.kt                  # expect 선언만
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
- Domain 레이어는 Supabase, Firebase 등 외부 의존성을 import하지 않는다.
- Repository interface는 Domain에, 구현체는 Data에 위치한다.
- UseCase는 단일 `invoke()` 연산자만 갖는다.
- 모든 DB 접근은 Repository를 통해서만 수행 (SupabaseClient 직접 쿼리 금지).

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

## 백엔드 연동

### 리포트 생성 흐름
```
앱 → GetTodayReportUseCase → FortuneRepository
  → Supabase DB 캐시 확인 (오늘 날짜)
  → 캐시 없으면: Edge Function `fortune` → Claude API → { grade, summary, advice } → DB 저장
  → 캐시 있으면: DB 레코드 반환
```

### AI API 응답 스펙
```json
{ "grade": "SUNNY", "summary": "한 문장 (20자 이내)", "advice": "오늘의 조언 (50자 이내)" }
```
grade 허용값: `SUNNY | CLEAR | CLOUDY | RAINY | STORM` (날씨 5단계, 자세한 톤 가이드 → `docs/PRD.md` 5절)

> **현재 사용 AI**: Gemini API (Google AI Studio 무료 티어 — Gemini 1.5 Flash)
> 출시 전 Claude API 교체 검토. API 호출부는 Edge Function에 격리되어 있어 교체 시 앱 코드 변경 없음.

### 푸시 알림 흐름
```
Supabase pg_cron (매분) → notify_time이 현재 시각인 유저 조회 → Edge Function → FCM
```

---

## DB 스키마

### users
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | UUID PK | Supabase Auth uid |
| kakao_id | TEXT UNIQUE | 카카오 식별자 |
| name | TEXT | 표시 이름 (필수, 최대 12자) |
| birth_date | DATE | 사주 계산용 (필수) |
| gender | TEXT | `MALE` / `FEMALE` (필수) |
| birth_time | TEXT | 태어난 시각 12시진 (`자`~`해`), null이면 정오 대표값 (선택) |
| notify_time | TIME | 알림 시각, null이면 비활성 |
| fcm_token | TEXT | 앱 실행 시 갱신 |
| created_at | TIMESTAMP | |

### fortunes
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | UUID PK | |
| user_id | UUID FK | users.id 참조 |
| date | DATE | user_id + date UNIQUE |
| grade | TEXT | `SUNNY / CLEAR / CLOUDY / RAINY / STORM` |
| summary | TEXT | 종합운 한 줄 요약 |
| advice | TEXT | 오늘의 조언 |
| created_at | TIMESTAMP | |

`fortunes`는 1일 보관. 새 날짜 리포트 생성 시 이전 레코드 삭제.

---

## 개발 순서

1. 프로젝트 세팅 (의존성, Supabase 프로젝트 생성)
2. DB 스키마 적용 (users, fortunes 테이블)
3. 카카오 로그인 (expect/actual + Supabase Auth 연동)
4. 운세 리포트 생성 (Edge Function + Claude API)
5. 리포트 화면 UI (TOAD + MVI)
6. 알림 (FCM + Supabase 스케줄러 + 설정 화면)
7. 배포 (Google Play / App Store)
