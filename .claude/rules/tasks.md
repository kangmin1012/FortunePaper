# FortunePaper 개발 태스크

> 상태: ⬜ 미시작 / 🔄 진행중 / ✅ 완료 / ⏸ 보류

---

## 준비사항 (외부 서비스)

| 상태 | 항목 | 비고 |
|------|------|------|
| ✅ | Supabase 프로젝트 생성 | Project URL · anon key · service_role key 확보 |
| ⬜ | Firebase 프로젝트 생성 | google-services.json · GoogleService-Info.plist 다운로드, FCM 활성화 |
| ✅ | 카카오 개발자 앱 등록 | Native 앱 키 확보, 패키지명 `com.fortune.paper` 등록, 카카오 로그인 활성화, Supabase 연동 완료 |
| ✅ | AI API 키 발급 | Gemini API 사용 (Google AI Studio 무료 티어) — Task 4에서 키 발급 후 Edge Function에 적용 |

---

## Task 1. 프로젝트 세팅

- [x] `libs.versions.toml` 의존성 추가
  - [x] Supabase KMP SDK
  - [x] Koin (KMP)
  - [x] Coil (KMP)
  - [x] Firebase KMP (FCM) — 버전 카탈로그 등록 완료, `build.gradle.kts` 추가는 Task 6(google-services.json 준비 후)
  - [x] Kotlin Serialization
- [x] Koin 모듈 초기 구조 생성 (`di/` 디렉토리)
- [x] 레이어 디렉토리 구조 생성 (`presentation/`, `domain/`, `data/`, `auth/`)
- [x] Supabase 클라이언트 초기화 (`SupabaseClientProvider.kt`)

---

## Task 2. DB 스키마

- [x] Supabase에서 `users` 테이블 생성
- [x] Supabase에서 `fortunes` 테이블 생성
- [x] `user_id + date` UNIQUE 제약 추가
- [x] RLS(Row Level Security) 정책 설정

---

## Task 3. 카카오 로그인

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
- [ ] (참고) `fortune` Edge Function 프롬프트에 `birth_time` 반영 (Task 4 연계, null이면 정오 대표값)

### Welcome/로그인 통합 (a안 확정 — 2026-06-02)
- [x] Task 3의 `LoginScreen`은 Welcome 화면(온보딩 0단계)으로 대체 — `App()`이 더 이상 `LoginScreen` 미사용 (`Login*` 파일은 잔존, 추후 정리 가능)
- [x] 로그인 성공 후 분기: 프로필 없으면 온보딩 1단계(Value)로 계속, 있으면 메인으로 (`OnboardingLoginAction` + `App()` 프로필 프로브)

---

## Task 4. 운세 리포트 생성

- [x] Supabase Edge Function `fortune` 작성 (Deno/TypeScript) — `supabase/functions/fortune/index.ts`
  - [x] Gemini API 호출 로직 (gemini-1.5-flash, responseSchema로 JSON 강제)
  - [x] `{ grade, summary, advice }` JSON 응답 (등급 화이트리스트 검증·길이 클램프)
  - [x] DB 저장 및 캐시 반환 로직 (당일 캐시 hit 반환 / 1일 보관: 과거 레코드 삭제 후 insert)
  - [x] `birth_time` 프롬프트 반영 (user_id로 서버 조회, null이면 정오 대표값) — PRD §8 연계 항목 해소
- [x] `FortuneRepository` 인터페이스 및 구현체 작성
- [x] `GetTodayReportUseCase` 작성
- [x] Edge Function 배포 및 테스트
  - [x] `GEMINI_API_KEY` Supabase 시크릿 등록 (다이제스트 확인, 레포에 미저장)
  - [x] `fortune` 배포 — STATUS ACTIVE (v1), `--project-ref zvqecylagvkznetltlpu`
  - [x] 도달성 스모크 테스트 — 무인증 POST 401(JWT 게이트), OPTIONS 200(CORS)
  - [ ] 실제 Gemini 생성 e2e — Task 5에서 로그인 유저가 `GetTodayReportUseCase` 호출 시 검증 (합성 유저 테스트는 프로덕션 오염 우려로 보류)

---

## Task 5. 리포트 화면 UI

- [x] `FortuneReport` 도메인 모델 정의
- [ ] `ReportState`, `ReportEvent`, `ReportDependencies` 작성
- [ ] Actions 작성
  - [ ] `LoadReport`
  - [ ] `RefreshReport`
- [ ] `ReportViewModel` 작성
- [ ] `ReportScreen` Composable 작성
  - [ ] 등급 표시 컴포넌트
  - [ ] 한 줄 요약 컴포넌트
  - [ ] 오늘의 조언 컴포넌트
  - [ ] 로딩/에러 상태 처리
- [ ] `App()` 에 화면 연결
- [ ] Koin 모듈 등록

---

## Task 6. 푸시 알림

- [ ] Firebase FCM 초기화 (Android / iOS)
- [ ] FCM 토큰 갱신 로직 구현 (`users.fcm_token` 업데이트)
- [ ] Supabase Edge Function `notify` 작성
- [ ] Supabase `pg_cron` 스케줄러 설정 (매분 실행)
- [ ] 알림 설정 화면 UI
  - [ ] `SettingsState`, `SettingsEvent`, `SettingsDependencies`
  - [ ] `UpdateNotifyTime` Action
  - [ ] `SettingsScreen` Composable (시간 선택 UI)
- [ ] `UpdateNotifyTimeUseCase` 작성
- [ ] Koin 모듈 등록

---

## Task 7. 배포

- [ ] Android
  - [ ] 릴리즈 키스토어 생성
  - [ ] `build.gradle.kts` 릴리즈 서명 설정
  - [ ] Google Play Console 앱 등록
  - [ ] AAB 빌드 및 업로드
- [ ] iOS
  - [ ] Apple Developer 앱 ID 등록
  - [ ] App Store Connect 앱 등록
  - [ ] Xcode 아카이브 및 업로드

---

## 미결정 사항

- [x] 운세 등급 체계 확정 → **날씨 5단계** (`SUNNY / CLEAR / CLOUDY / RAINY / STORM`)
- [ ] 수익 모델 확정 (출시 후 반응 보고 결정)
- [ ] 커스텀 디자인 파일 적용 시점
