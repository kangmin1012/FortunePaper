# FortunePaper 개발 태스크

> 상태: ⬜ 미시작 / 🔄 진행중 / ✅ 완료 / ⏸ 보류·폐기

> **🔄 v1.1 전환 (2026-06-10)**: 카카오 로그인·서버 유저 DB를 제거하고 로컬 프로필(DataStore) + 로컬 알림으로 전환한다.
> 기획 명세 → [`docs/spec-local-user-migration.md`](../../docs/spec-local-user-migration.md)
> Task 1~5는 v1.0 기준 완료 이력으로 보존한다. 카카오·서버 DB 관련 산출물은 **Task 8에서 제거/대체**된다. Task 6(FCM 푸시)은 폐기되었고, 신규 작업은 **Task 8 시리즈** → Task 7(배포) 순서로 진행한다.

---

## 준비사항 (외부 서비스)

| 상태 | 항목 | 비고 |
|------|------|------|
| ✅ | Supabase 프로젝트 생성 | Project URL · anon key 확보 — v1.1부터 Edge Function `fortune` 호스팅 용도만 |
| ⏸ | ~~Firebase 프로젝트 생성~~ | v1.1 폐기 — 로컬 알림 전환으로 FCM 불필요 |
| ⏸ | ~~카카오 개발자 앱 등록~~ | v1.0에서 완료했으나 v1.1 폐기 — Task 8.6에서 연동 제거 |
| ✅ | AI API 키 발급 | Gemini API 사용 (Google AI Studio 무료 티어) — Edge Function 시크릿 등록 완료 |

---

## Task 1. 프로젝트 세팅

- [x] `libs.versions.toml` 의존성 추가
  - [x] Supabase KMP SDK — ⚠️ v1.1: Task 8.6에서 auth·postgrest 제거, functions만 유지
  - [x] Koin (KMP)
  - [x] Coil (KMP)
  - [x] Firebase KMP (FCM) — ⚠️ v1.1 폐기: Task 8.6에서 카탈로그 등록 제거
  - [x] Kotlin Serialization
- [x] Koin 모듈 초기 구조 생성 (`di/` 디렉토리)
- [x] 레이어 디렉토리 구조 생성 (`presentation/`, `domain/`, `data/`, `auth/`)
- [x] Supabase 클라이언트 초기화 (`SupabaseClientProvider.kt`)

---

## Task 2. DB 스키마

> ⚠️ **v1.1 폐기 예정** — 서버 유저 DB를 사용하지 않는다. Task 8.6에서 `users`·`fortunes` 테이블 DROP.

- [x] Supabase에서 `users` 테이블 생성
- [x] Supabase에서 `fortunes` 테이블 생성
- [x] `user_id + date` UNIQUE 제약 추가
- [x] RLS(Row Level Security) 정책 설정

---

## Task 3. 카카오 로그인

> ⚠️ **v1.1 제거 예정** — 로그인 개념이 사라짐. 아래 산출물 전체(KakaoAuth expect/actual, Swift 브리지, `kakao-auth` Edge Function, Login* 파일, 카카오 SDK 의존성)는 Task 8.6에서 삭제된다. 완료 이력으로만 보존.

- [x] `KakaoAuth.kt` expect 선언 작성
- [x] `KakaoAuth.android.kt` actual 구현 (카카오 Android SDK)
- [x] `KakaoAuth.ios.kt` actual 구현 (카카오 iOS SDK) — Swift 브리지 패턴, 시뮬레이터 빌드 검증 완료(BUILD SUCCEEDED)
  - Kotlin: `KakaoAuthBridge`(인터페이스) + `KakaoBridgeHolder`(주입 홀더) + `IosApp.kt`(`startApp` 부트스트랩: Koin+Supabase+브리지)
  - Swift: `KakaoAuthBridgeImpl.swift`(SDK 호출) + `iOSApp.swift`(`initSDK`/`startApp`/`onOpenURL`) + `Info.plist`(URL scheme·쿼리 스킴) + `Secrets.xcconfig`(앱-사이드 키, gitignore)
  - 외부 준비 완료: ① 카카오 콘솔 iOS 플랫폼(번들ID `com.fortune.paper`) 등록 ② Xcode SPM `https://github.com/kakao/kakao-ios-sdk` 추가(KakaoSDKCommon/Auth/User)
  - 참고: `init*` 접두 함수는 ObjC 인터롭에서 이니셜라이저로 취급되어 Swift 노출 안 됨 → `startApp`으로 명명
- [x] Supabase Auth 커스텀 토큰 교환 로직 구현 (`kakao-auth` Edge Function + `signInWithKakao`)
- [x] `UserRepository` 인터페이스 및 구현체 작성
- [x] 로그인 화면 UI (TOAD + MVI)
  - [x] `LoginState`, `LoginEvent`, `LoginDependencies`
  - [x] `KakaoLoginAction`
  - [x] `LoginScreen` Composable — ⚠️ Task 3.5에서 Welcome 화면으로 대체 예정 (로직은 재사용)
- [x] Koin 모듈 등록

---

## Task 3.5. 온보딩 (첫 실행 플로우)

> ⚠️ **v1.1 일부 변경 예정** — Welcome의 카카오 로그인 버튼은 "시작하기"로 교체(Task 8.3), `OnboardingLoginAction`·`KakaoButton`·`kakaoId`는 제거(Task 8.6), `SubmitOnboarding`은 서버 upsert → 로컬(DataStore) 저장으로 전환(Task 8.3). 나머지 단계 UI·TOAD 구조는 그대로 재사용.

> 디자인 기준: `design/FortunePaper_Design/Onboarding.html` · `screens.jsx`
> 단일 디바이스 안에서 진행되는 **7단계 스텝 플로우**. 상단 진행바 → 본문 → 하단 CTA의 일관된 리듬(`StepShell`). 마지막 "완료하기" → 별도 계산/공개 화면 없이 곧바로 메인 화면 진입.
> `STEPS = ['welcome', 'value', 'name', 'birth', 'gender', 'time', 'notify']`

### 공통 골격 (shared chrome) — `presentation/onboarding/components/`
- [x] `StepShell` — 상단바(뒤로 ‹/진행바/건너뛰기) + 본문 + 하단 CTA 레이아웃
- [x] `OnboardingProgressBar` — 7단계용 얇은 진행 바 (`step.progress` 비율)
- [x] `FPButton` — 기본 CTA 버튼 (`enabled`/`loading` 상태 포함)
- [x] `WheelPicker` — 휠 피커 (생년월일용, LazyColumn + 중앙 밴드)
- [x] `KakaoButton` — 카카오 색(#FEE500) 버튼
- [x] `GradeStrip`/`HeroGradeIcon` — 날씨 등급 이모지 (SVG 대신 이모지로 대체)

### 단계별 화면 — `presentation/onboarding/screens/`
- [x] **0. Welcome** — 브랜드 소개(히어로 ☀️ + 5단계 날씨 스트립) + **카카오 로그인 버튼** + 약관 고지
  - Task 3의 `LoginScreen`은 Welcome으로 대체됨 (a안). 로그인 로직은 `OnboardingLoginAction`으로 재구현
- [x] **1. Value** — 가치 제안 3카드 (한 장의 리포트 / 사주 기반 / 매일 아침)
- [x] **2. Name** — 이름 입력 (필수, 최대 12자) → `users.name` 저장
- [x] **3. Birth** — 생년월일 휠 피커 (년 1950–2010 / 월 / 일), 필수
- [x] **4. Gender** — 성별 음양 카드 (여=음 / 남=양), 필수
- [x] **5. Time** — 태어난 시각 12시진(자~해), **선택 입력** (건너뛰기/"잘 모르겠어요") → `users.birth_time`(미선택 시 null)
- [x] **6. Notify** — 알림 시간 4프리셋(06:30/07:30/08:30/09:30, 기본 07:30) → "완료하기" → `users.notify_time`

### TOAD/MVI 구조 — `presentation/onboarding/`
- [x] `OnboardingState` — `step`(enum) + 수집 필드(name, birth Y/M/D, gender, birthTime?, notifyTime, kakaoId) + `canProceed` 유효성
- [x] `OnboardingEvent` — `NavigateToMain`, `ShowError`
- [x] `OnboardingDependencies`
- [x] Actions (1액션 1파일)
  - [x] `GoToNextStep` / `GoToPreviousStep` (단계 이동)
  - [x] `SetName` / `SetBirthDate` / `SetGender` / `SetBirthTime` / `SetNotifyTime`
  - [x] `OnboardingLoginAction` — 카카오 로그인 → 기존 유저는 메인, 신규는 Value로
  - [x] `SubmitOnboarding` — 필수값 검증 → `saveUser` + `updateNotifyTime` → `NavigateToMain`
- [x] `OnboardingViewModel` 작성
- [x] `OnboardingScreen` Composable (`StepShell` 기반 단계 렌더링)
- [x] `App()` 분기 연결 (로그인 후 프로필 없으면 온보딩, 있으면 메인) + `AppTheme`(Noto Sans KR + 디자인 토큰) 적용
- [x] Koin 모듈 등록 (`OnboardingViewModel`)

### 데이터 모델 변경 (A안 확정 — 디자인 우선, 2026-06-02)
> PRD §8 · architecture.md DB 스키마 갱신 완료. Task 2(DB 스키마)는 아래 마이그레이션으로 보강 필요.
- [x] 마이그레이션 SQL 작성 — `supabase/migrations/20260602093000_add_name_and_birth_time_to_users.sql` (name NOT NULL + birth_time, CHECK 제약 포함)
- [x] **위 마이그레이션 원격 적용** — 대시보드 SQL Editor로 적용 완료 (2026-06-02). `users.name`·`birth_time` 컬럼 생성 확인
- [x] `UserDto` · `UserUpsert`에 `name` · `birth_time` 필드 추가
- [x] `UserRepository.saveUser` / `UserRemoteDataSource.upsertUser` 시그니처에 `name` · `birthTime` 추가 + `User` 도메인 모델/`toDomain` 매핑 갱신
- [x] (참고) `fortune` Edge Function 프롬프트에 `birth_time` 반영 (Task 4에서 해소, null이면 정오 대표값)

### Welcome/로그인 통합 (a안 확정 — 2026-06-02)
- [x] Task 3의 `LoginScreen`은 Welcome 화면(온보딩 0단계)으로 대체 — `App()`이 더 이상 `LoginScreen` 미사용 (`Login*` 파일은 잔존, 추후 정리 가능)
- [x] 로그인 성공 후 분기: 프로필 없으면 온보딩 1단계(Value)로 계속, 있으면 메인으로 (`OnboardingLoginAction` + `App()` 프로필 프로브)

---

## Task 4. 운세 리포트 생성

> ⚠️ **v1.1 개편 예정** — Edge Function의 DB 캐시·user_id 조회는 Task 8.4에서 stateless 방식으로 교체된다.

- [x] Supabase Edge Function `fortune` 작성 (Deno/TypeScript) — `supabase/functions/fortune/index.ts`
  - [x] Gemini API 호출 로직 (gemini-2.5-flash, responseSchema로 JSON 강제) — gemini-1.5-flash는 구글 retire로 404, 2026-06-14 교체
  - [x] `{ grade, summary, advice }` JSON 응답 (등급 화이트리스트 검증·길이 클램프)
  - [x] DB 저장 및 캐시 반환 로직 (당일 캐시 hit 반환 / 1일 보관: 과거 레코드 삭제 후 insert)
  - [x] `birth_time` 프롬프트 반영 (user_id로 서버 조회, null이면 정오 대표값) — PRD §8 연계 항목 해소
- [x] `FortuneRepository` 인터페이스 및 구현체 작성
- [x] `GetTodayReportUseCase` 작성
- [x] Edge Function 배포 및 테스트
  - [x] `GEMINI_API_KEY` Supabase 시크릿 등록 (다이제스트 확인, 레포에 미저장)
  - [x] `fortune` 배포 — STATUS ACTIVE (v1), `--project-ref zvqecylagvkznetltlpu`
  - [x] 도달성 스모크 테스트 — 무인증 POST 401(JWT 게이트), OPTIONS 200(CORS)
  - [ ] 실제 Gemini 생성 e2e — **Task 8.5로 이관** (stateless 개편 후 검증이 합리적)

---

## Task 5. 리포트 화면 UI

> 디자인 기준: `design/FortunePaper_Design/daily-entry.jsx`. 사전 작업으로 디자인 시스템을 코드화(아래) 후 진행.
> Android + iOS 빌드 통과 검증 완료(BUILD SUCCESSFUL, 에러·경고 없음).

### 사전 작업 — 디자인 시스템 (`presentation/theme/`)
- [x] `FortuneColors` 등급 headline 색 추가(GRADES 기준) + `GradeColors.kt`(`color()`/`headlineColor()` 매퍼)
- [x] `FortuneDimens.kt`(Spacing/Radius) · `FortuneShapes.kt`(→ MaterialTheme.shapes) · `FortuneType.kt`(타입 스케일)
- [x] `AppTheme` 에 shapes 연결
- [x] 문서화 `docs/design-system.md`

### 화면
- [x] `FortuneReport` 도메인 모델 정의
- [x] `ReportState`, `ReportEvent`, `ReportDependencies` 작성
- [x] Actions 작성
  - [x] `LoadReport` (캐시 우선, 중복 로드 방지)
  - [x] `RefreshReport` (실패해도 기존 리포트 유지)
- [x] `ReportViewModel` 작성
- [x] `ReportScreen` Composable 작성 (`ReportComponents.kt` 분리)
  - [x] 등급 표시 컴포넌트 (hero 이모지 + 등급명 headline 색)
  - [x] 한 줄 요약 컴포넌트 (체크 배지 + summary 카드)
  - [x] 오늘의 조언 컴포넌트 (accent bar + advice 카드)
  - [x] 로딩/에러 상태 처리 (계산중 로딩, 오류+재시도) + HomeShell(내비 새로고침/하단 탭바)
- [x] `App()` 에 화면 연결 (`onboarded -> ReportScreen()`)
- [x] Koin 모듈 등록 (`ReportViewModel`)
- 보류 리소스(SVG 아이콘·정교 연출 등)는 `docs/deferred-resources.md` 기록

---

## ~~Task 6. 푸시 알림~~ — ⏸ 폐기 (v1.1)

> **Task 8.7(설정 화면)·8.8(로컬 알림)로 대체됨.** 로컬 알림 전환으로 Firebase/FCM/pg_cron/`notify` Edge Function이 모두 불필요해짐 (서버 측 자산은 애초 미구현이라 삭제 대상 없음). 원래 항목은 이력으로만 보존:
>
> ~~Firebase FCM 초기화 / FCM 토큰 갱신 / Edge Function `notify` / pg_cron 스케줄러 / 알림 설정 화면 / UpdateNotifyTimeUseCase / Koin 등록~~

---

## Task 8. 로컬 전환 마이그레이션 (v1.1)

> 기획 명세: [`docs/spec-local-user-migration.md`](../../docs/spec-local-user-migration.md)
> **순서 원칙**: 각 단계 완료 시점에 빌드 가능 상태를 유지한다. 카카오·서버 잔재 제거(8.6)는 대체 경로(8.3·8.5)가 완성된 뒤에만 수행.

### Task 8.1. DataStore 기반 구축 — ✅ 완료 (2026-06-10)

- [x] `libs.versions.toml`에 `datastore-preferences-core` (androidx.datastore 1.1.7, KMP) 등록 + shared commonMain 적용
- [x] `data/local/DataStoreFactory.kt` — 공통 `createDataStore(producePath)` + androidMain(`Context.filesDir`) / iosMain(`NSDocumentDirectory`) 오버로드 — 파일명 `fortune_paper.preferences_pb`
  - expect/actual 대신 공통 팩토리 + 플랫폼 오버로드 패턴 사용 (플랫폼별 파라미터가 달라 expect/actual 불가)
- [x] Koin `platformModule` 구성 (androidMain: `di/AndroidModule.kt` / iosMain: `IosApp.kt`) — `DataStore<Preferences>` + `LocalNotifier` single 제공

### Task 8.2. 로컬 데이터 레이어 — ✅ 완료 (2026-06-10)

- [x] `UserProfile` 도메인 모델 신설 (name, birthDate, gender, birthTime?, notifyEnabled, notifyTime) — 기존 `User` 삭제, `Gender`는 `UserProfile.kt`로 이동
- [x] `UserLocalDataSource` — 프로필·알림 설정 read/write, `observeProfile(): Flow<UserProfile?>` (키 스키마 → architecture.md)
- [x] `FortuneLocalDataSource` — 운세 캐시(`fortune_cache_json`) read/write/clear (`FortuneCache` @Serializable)
- [x] `UserRepository` 인터페이스 개편 — `observeProfile` / `getProfile` / `saveProfile` / `updateNotifySettings` / `clearAll` (loginWithKakao·signOut 등 제거)
- [x] `UserRepositoryImpl` 로컬 구현 교체 + `UpdateNotifyTimeUseCase`(enabled+time 시그니처로 개편) 연결 유지
- [x] 신규 UseCase — `SaveProfileUseCase`(사주 변경 시 캐시 무효화 포함), `ResetAppDataUseCase`
- [x] Koin 모듈 갱신

### Task 8.3. 온보딩 · App 분기 전환

> 디자인 기준 확보 (2026-06-10): `screens.jsx`의 Welcome 시안이 카카오 버튼 → `FPButton("시작하기")`로 갱신됨.

- [x] Welcome 화면: 카카오 로그인 버튼 → `FPButton("시작하기")` (`GoToNextStep` 디스패치) — ✅ 완료 (2026-06-10)
- [x] `OnboardingState`에서 `kakaoId`·인증 상태 제거, Welcome의 `canProceed = true`
- [x] `SubmitOnboarding` 전환 — 필수값 검증 → `saveProfile`(로컬) + 알림 설정 저장 + 권한 요청·알림 예약 → `NavigateToMain`
- [x] `App.kt` 분기 교체 — `sessionStatus` 감시 → `observeProfile()` Flow (Loading → 부재=온보딩 / 존재=리포트·설정, 초기화 시 Flow null 방출로 자동 복귀)

### Task 8.4. fortune Edge Function stateless 개편 — ✅ 완료 (배포 2026-06-14, ACTIVE v3)

> 원격 작업 절차 → [`docs/followup-local-migration.md`](../../docs/followup-local-migration.md)

- [x] 요청 스펙 변경 — `{ user_id }` → `{ birth_date, gender, birth_time }` (name 미전송, 입력값 검증 추가)
- [x] 응답 스펙 변경 — `{ date, grade, summary, advice }` (id/user_id/created_at 제거)
- [x] DB 캐시 조회·insert·삭제 및 supabase-js 의존 제거 (Gemini 호출 + 검증·클램프만 잔존)
- [x] `verify_jwt = false` **배포** + 스모크 테스트 — 2026-06-14 배포(ACTIVE v3). ⚠️ 실측: apikey 미포함도 함수 도달(verify_jwt=false가 apikey 게이트까지 끔, 무인증 공개 엔드포인트 — followup §1.1). 모델은 `gemini-1.5-flash` 404 → `gemini-2.5-flash` 교체.
- [x] `kakao-auth` 삭제 — 로컬 디렉토리 삭제 완료 + 원격은 이미 부재 확인(2026-06-14)

### Task 8.5. 리포트 흐름 전환 (8.2 + 8.4 의존) — ✅ 서버 e2e 완료 (앱 통합 e2e 선택 잔존)

- [x] `FortuneDto`·`FortuneRemoteDataSource` 신스펙 반영 (postgrest 캐시 조회 제거, functions.invoke만)
- [x] `FortuneRepositoryImpl` 재구현 — 로컬 캐시 `date == 오늘(KST)` 시 반환(재호출 금지 강제) / 미스 시 프로필 읽어 호출 후 캐시 저장
- [x] `FortuneRepository`에 `invalidateCache()` 추가 (8.7 프로필 수정 연계) + `FortuneReport` 도메인 모델에서 `id` 제거
- [x] **Gemini 생성 e2e (서버)** — 배포본에 직접 curl 호출로 실 운세 생성 확인 (2026-06-14). 앱 레벨 통합 확인은 선택 잔존 (`docs/followup-local-migration.md` §2)

### Task 8.6. 카카오 · 서버 잔재 완전 제거 (8.3 + 8.5 완료 후)

> 상세 삭제 목록 → `docs/spec-local-user-migration.md` §6

- [x] shared: `auth/` 전체, `presentation/login/` 전체, `OnboardingLoginAction`, `KakaoButton`(+kakao 컬러 토큰), `UserRemoteDataSource`(UserDto 포함), `SupabaseClientProvider`의 Auth·Postgrest 설치 제거 (Functions만 잔존)
- [x] androidApp: `KakaoSdk.init`·`KakaoAuthHolder` 연결, Manifest의 kakao queries·AuthCodeHandlerActivity, gradle kakao 의존성·`KAKAO_NATIVE_APP_KEY` 제거
- [x] iosApp: `KakaoAuthBridgeImpl.swift`, `iOSApp.swift`의 KakaoSDK/onOpenURL/브리지 인자, `Info.plist` kakao 스킴, `Secrets.xcconfig` 키, Xcode SPM `kakao-ios-sdk`(pbxproj) 제거 + 카카오 핀 남은 `Package.resolved` 삭제(재생성)
- [x] `libs.versions.toml`: kakao·firebase·supabase-auth·supabase-postgrest 제거 (supabase-functions 유지)
- [x] DB 정리: `users`·`fortunes` DROP 마이그레이션 **원격 적용 완료** (`db push --linked`, 2026-06-14) — [ ] Supabase Auth 기존 카카오 사용자 정리는 대시보드에서 수동 수행 잔존 (`docs/followup-local-migration.md` §1.4)
- [x] `local.properties`: `kakao.nativeAppKey` 제거 + **`supabase.secretKey` 제거 (secrets.md 위반 해소)** — 키 롤테이션은 대시보드에서 검토 (followup §1.5)
- [x] Android(`assembleDebug`) + iOS(시뮬레이터, BUILD SUCCEEDED) 빌드 검증 (2026-06-10)

### Task 8.7. 설정 화면 — 내 정보 · 알림 · 정보 초기화 (8.2 의존)

> 디자인 기준 확보 (2026-06-10): `settings.jsx`/`Settings.html` 시안 갱신 완료 — 설정 목록 3항목(**내 정보** / 알림 설정 / **정보 초기화**), `ProfileEditScreen`(이름 입력 + 생년월일 휠 + 성별 음양 카드 + 12시진 그리드·"잘 모르겠어요" + "저장하기" CTA), 초기화 확인 다이얼로그. UI 라벨은 시안 기준 "내 정보"·"정보 초기화" 사용. 흐름 데모는 `Settings.standalone.html`·`screenshots/settings-*.png` 참고.

- [x] (선행 디자인) `settings.jsx`/`Settings.html` 시안 갱신 — 내 정보 편집 추가, 로그아웃·회원 탈퇴 → 정보 초기화 (2026-06-10 디자인 반영 확인)
- [x] TOAD 구조 — `SettingsState`(view: List/ProfileEdit/NotifyEdit + 편집 초안), `SettingsEvent`, `SettingsDependencies`, `SettingsViewModel`, `SettingsScreen` — ✅ 완료 (2026-06-10)
- [x] Actions (1액션 1파일) — `LoadSettings` / `ShowSettingsView` / `SetDraft*`(Name·Birth·Gender·BirthTime·NotifyTime) / `UpdateNotifyTime` / `ToggleNotify` / `SaveProfile` / `SetResetDialog` / `ResetAppData`
- [x] 설정 목록 화면 — 내 정보(현재 이름 meta 표시) / 알림 설정(현재 시각 meta, 꺼짐 표시) / 정보 초기화(destructive) + 버전 푸터 (버전 라벨은 하드코딩 — TODO: followup §3)
- [x] 내 정보 편집 UI — 온보딩 `WheelPicker` 재사용 + 시안의 `ProfileEditScreen` 기준 (이름 비우면 저장 비활성, 뒤로가기 시 편집 중 값 폐기 — 진입 시 초안 재시드)
- [x] 사주 입력값(생년월일·성별·시진) 변경 감지 → `invalidateCache()` 호출 (이름만 변경 시 캐시 유지 — `SaveProfileUseCase`에서 강제)
- [x] 정보 초기화 — 확인 다이얼로그(시안 문구 기준) → `clearAll()` + 알림 취소 → 온보딩 복귀 (Flow 분기로 자동)
- [x] `ReportScreen` 하단 탭바에서 설정 진입 연결 (`App.kt` showSettings 분기) + Koin 등록

### Task 8.8. 로컬 알림 (8.1 의존, 온보딩 연동은 8.3 · 설정 연동은 8.7) — ✅ 완료 (2026-06-10)

- [x] `platform/notification/LocalNotifier.kt` expect — `requestPermission()` / `scheduleDaily(hour, minute)` / `cancel()` + `parseNotifyTime` 헬퍼
- [x] Android actual — `AlarmManager.setAndAllowWhileIdle`(inexact) + `DailyFortuneAlarmReceiver`(알림 표시 후 익일 재예약) + `BootCompletedReceiver`(BOOT_COMPLETED 재예약) + API 33+ `POST_NOTIFICATIONS` 권한(`NotificationPermissionRequester` 브리지 ← MainActivity)
- [x] iOS actual — `UNUserNotificationCenter` + `UNCalendarNotificationTrigger(repeats: true)` + `requestAuthorization`
- [x] 알림 고정 문구 정의 — "오늘의 포춘페이퍼가 도착했어요 📰" (등급 미포함 — PRD §4.4)
- [x] 온보딩 완료 시 권한 요청 + 예약(`SubmitOnboarding`), 설정 토글·시간 변경 시 재예약/취소 연동(`ToggleNotify`/`UpdateNotifyTime`)
- [x] Koin 등록 (platformModule)

---

## Task 7. 배포 (Task 8 완료 후)

> 앱 아이콘 시안 확보 (2026-06-10): `App Icon.html`(iOS) · `App Icon - Android.html`(Android) — 선택안 D "운세 한 장"(해 + 리포트 카드). `screenshots/app-icon.png` 참고.

- [x] 앱 아이콘 리소스 적용 (2026-06-14)
  - [x] Android — adaptive icon 적용 완료. 전경/배경 PNG(1024)를 `mipmap-{mdpi~xxxhdpi}/ic_launcher_foreground.png`·`ic_launcher_background.png` 5밀도로 리사이즈, `mipmap-anydpi-v26/ic_launcher*.xml`이 `@mipmap` 레이어 참조, 레거시 폴백(`ic_launcher.png`/`_round.png`)은 평탄화 아이콘(512)에서 생성. 기본 템플릿 vector(`drawable-v24/ic_launcher_foreground.xml`·`drawable/ic_launcher_background.xml`) 제거. Play Store 512 아이콘은 `androidApp/playstore/play-store-icon-512.png` 보관. `assembleDebug` 검증·APK 패키징 확인
  - [x] iOS — `Assets.xcassets/AppIcon.appiconset/app-icon-1024.png`를 평탄화(알파 제거, App Store 요건) 1024 아이콘으로 교체. Contents.json 구조 불변(동일 파일명·치수). 최종 검증은 Xcode 아카이브 시 수행 권장
- [ ] Android
  - [ ] 릴리즈 키스토어 생성
  - [ ] `build.gradle.kts` 릴리즈 서명 설정
  - [ ] Google Play Console 앱 등록
  - [ ] AAB 빌드 및 업로드
- [ ] iOS
  - [ ] 카카오 SPM·키 잔재 없음 최종 확인 (Task 8.6 재검증)
  - [ ] Apple Developer 앱 ID 등록
  - [ ] App Store Connect 앱 등록
  - [ ] Xcode 아카이브 및 업로드

---

## 미결정 사항

- [x] 운세 등급 체계 확정 → **날씨 5단계** (`SUNNY / CLEAR / CLOUDY / RAINY / STORM`)
- [x] 유저 정보 저장 방식 확정 → **로컬(DataStore) 저장, 계정 없음** (v1.1, 2026-06-10 — `docs/spec-local-user-migration.md`)
- [ ] 수익 모델 확정 (출시 후 반응 보고 결정)
- [ ] 커스텀 디자인 파일 적용 시점
