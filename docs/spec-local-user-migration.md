# 변경 기획 명세 — 로컬 유저 전환 (v1.1)

> **작성**: 2026-06-10 | **상태**: 확정 | **적용 버전**: PRD v1.1
> 관련 문서: [`.claude/rules/prd.md`](../.claude/rules/prd.md) · [`.claude/rules/architecture.md`](../.claude/rules/architecture.md) · [`.claude/rules/tasks.md`](../.claude/rules/tasks.md)

---

## 1. 배경 / 동기

v1.0은 카카오 로그인 → Supabase Auth 계정 → 서버 DB(`users` 테이블)에 유저 정보를 저장하는 구조였다. 그러나:

- 포춘페이퍼의 핵심 가치는 **"30초 리추얼"** — 계정·로그인은 첫 진입 마찰만 키우고, v1 기능(개인 운세 열람)에는 계정 간 공유·멀티 디바이스가 필요 없다.
- 서버에 사주 정보(생년월일·성별)를 영속 저장하는 것은 개인정보 부담만 늘린다. 운세 생성에는 요청 시점에 한 번 전달하면 충분하다.
- 카카오 SDK + Supabase Auth + 커스텀 토큰 교환(Edge Function)의 유지 비용이 v1 규모 대비 과하다.

**변경 결정**: 계정/로그인 개념을 제거하고, 온보딩에서 수집한 유저 정보를 **기기 로컬(DataStore)** 에 저장·관리한다. 서버는 운세 생성만 담당하는 **무상태(stateless) AI 프록시**로 축소한다.

---

## 2. Before → After 아키텍처

### Before (v1.0)

```
카카오 SDK 로그인
  → Edge Function `kakao-auth` (토큰 검증 + Supabase Auth 계정 생성/로그인)
  → Supabase 세션 (sessionStatus로 화면 분기)
  → 온보딩 입력 → users 테이블 upsert (서버 저장)
  → 리포트: Edge Function `fortune` ← user_id로 users 조회 → Gemini → fortunes 테이블 캐시
  → 알림: pg_cron(매분) → Edge Function `notify` → FCM (계획, 미구현)
```

### After (v1.1)

```
앱 실행 → 로컬 프로필(DataStore) 존재 여부로 분기
  ├─ 없음 → 온보딩 (Welcome "시작하기" → 정보 입력) → DataStore 저장 → 메인
  └─ 있음 → 리포트 화면
리포트: 로컬 캐시(date=오늘 KST) 있으면 즉시 반환
  └─ 없으면: Edge Function `fortune`(stateless) ← { birth_date, gender, birth_time }
       → Gemini → { date, grade, summary, advice } → DataStore 캐시 저장
알림: 기기 로컬 알림 — AlarmManager(Android) / UNUserNotificationCenter(iOS)
설정: 프로필 수정(신규) · 알림 설정 · 데이터 초기화
```

서버 잔존물은 **Edge Function `fortune` 하나**뿐이다. DB 테이블·Auth·pg_cron·FCM 모두 제거된다.

---

## 3. 확정 결정사항

| # | 결정 | 내용 | 근거 |
|---|------|------|------|
| D1 | 유저 정보 저장 | 이름·생년월일·성별·시진·알림설정 전부 **DataStore(KMP)** 로컬 저장. 서버 미저장 | 계정 불필요, 개인정보 최소화 |
| D2 | 운세 캐시 | 서버(`fortunes` 테이블) 캐시 폐기 → **로컬 DataStore 캐시 단일화**. 캐시에 `date`(KST) 포함, 오늘과 불일치 시 재생성 | 서버 완전 stateless화로 DB/RLS 관리 소멸 (§4.1 트레이드오프) |
| D3 | fortune 요청 스펙 | `{ user_id }` → `{ birth_date, gender, birth_time }`. `name`은 프롬프트 미사용이므로 전송 안 함 | 서버에 유저 없음, 개인정보 최소화 |
| D4 | fortune 응답 스펙 | `{ date, grade, summary, advice }` — `id`/`user_id`/`created_at` 제거 | DB 레코드 개념 소멸 |
| D5 | Edge Function 인증 | `verify_jwt = false`로 배포. Supabase 게이트웨이의 apikey 검사는 유지 | Auth 제거로 사용자 JWT 부재. publishable key는 JWT가 아니라 verify_jwt 게이트 통과 불가 |
| D6 | 프로필 수정 | **설정 화면에서 수정 가능** (v1.0의 "수정 불가, 탈퇴 후 재가입" 정책 폐기) | 신규 요구사항 |
| D7 | 수정 시 캐시 무효화 | 사주 입력값(생년월일·성별·시진) 변경 시에만 당일 운세 캐시 삭제 → 재생성. **이름만 변경 시 유지** | 사주가 바뀌면 운세도 바뀌어야 신뢰성 유지. "하루 1회 생성" 원칙의 명시적 예외 |
| D8 | 알림 방식 | Firebase/FCM/pg_cron 폐기 → **로컬 알림**. Android: `AlarmManager.setAndAllowWhileIdle`(inexact) + 발송 후 재예약 + `BOOT_COMPLETED` 재예약 + `POST_NOTIFICATIONS` 권한. iOS: `UNCalendarNotificationTrigger(repeats: true)` | §4.2 트레이드오프 |
| D9 | 알림 문구 | 고정 문구만 (예: "오늘의 포춘페이퍼가 도착했어요 📰"). v1.0의 "고정 문구 + 당일 등급" 폐기 | 발송 시점에 당일 운세가 아직 생성 전 |
| D10 | 알림 설정 모델 | `notify_enabled`(토글)와 `notify_time`(시각)을 분리 저장 | 끄기/켜기 시 시각이 보존됨 (v1.0은 `notify_time = null`로 비활성 표현) |
| D11 | 온보딩 완료 판단 | 별도 플래그 없이 필수 3종(name·birth_date·gender) 존재 여부로 판단 | 이중 상태 방지 |
| D12 | 설정 화면 구성 | 프로필 수정(신규) / 알림 설정 / **데이터 초기화**(로그아웃·회원 탈퇴 대체) / 버전 표기 | 계정이 없으므로 로그아웃·탈퇴 개념 소멸 |
| D13 | 기존 카카오 코드 | **완전 삭제** (git 히스토리로 보존). 유료화 등으로 계정이 필요해지면 그때 재설계 | 죽은 코드 미보관 |

---

## 4. 트레이드오프 기록

### 4.1 운세 캐시: 로컬 단일화 vs 디바이스 ID 기반 서버 캐시

| 기준 | 채택: 로컬(DataStore) 캐시만 | 기각: 디바이스 ID 서버 캐시 |
|------|------|------|
| 서버 복잡도 | fortunes 테이블·RLS·service_role 전부 제거 | 테이블 유지 + device_id 마이그레이션 필요 |
| Gemini 남용 방지 | 클라이언트 캐시에 의존 — 재설치 시 재호출 가능 | 동일 device_id 재호출은 캐시로 차단 |
| 하루 1회 일관성 | 재설치·데이터 초기화 시 같은 날 다른 결과 가능 | 디바이스 단위 보장 |
| 클라이언트 의존성 | supabase-postgrest 제거 가능 (functions만 잔존) | postgrest 유지 |

**채택 근거**: v1은 무료·소규모이고 Gemini 무료 티어라 남용 비용이 작다. "재설치 시 운세가 달라질 수 있음"은 제품상 수용 가능. 단, **클라이언트는 당일 캐시 존재 시 절대 재호출하지 않는 규칙을 Repository 레벨에서 강제**한다.
**재검토 조건**: Gemini 호출 남용이 실측되면 디바이스 ID 서버 캐시 또는 IP rate limit 재도입 (PRD §12 등재).

### 4.2 Android 알림: inexact vs exact alarm

| 방식 | 정확도 | 비고 |
|------|--------|------|
| 채택: `setAndAllowWhileIdle` (inexact) | 수 분 오차 | **특수 권한 불필요** |
| 기각: `setExactAndAllowWhileIdle` | 정확 | Android 12+ `SCHEDULE_EXACT_ALARM` 특수 권한(설정 화면 유도) 필요. 13+의 `USE_EXACT_ALARM`은 알람/캘린더 앱 한정 — 운세 앱은 Play 정책 리젝 위험 |
| 기각: WorkManager Periodic | 15분+ 지연 | 아침 알림에 오차 과대 |

**채택 근거**: 아침 운세 알림은 분 단위 정밀도가 불필요하고, 권한 마찰·스토어 정책 리스크 회피가 더 중요하다.

---

## 5. 로컬 데이터 스키마 (DataStore)

파일: `fortune_paper.preferences_pb` (androidx.datastore preferences)

| 키 | 타입 | 설명 |
|----|------|------|
| `profile_name` | String | 이름 (필수, 최대 12자) |
| `profile_birth_date` | String | 생년월일 YYYY-MM-DD (필수) |
| `profile_gender` | String | `MALE` / `FEMALE` (필수) |
| `profile_birth_time` | String? | 태어난 시각 12시진 (`자`~`해`), 미설정 시 없음 → 서버가 정오 대표값 처리 |
| `notify_enabled` | Boolean | 알림 토글 (기본 true) |
| `notify_time` | String | 알림 시각 HH:mm (기본 07:30) |
| `fortune_cache_json` | String | 당일 운세 캐시 — `{ date, grade, summary, advice }` JSON 직렬화 |

---

## 6. 삭제 자산 전체 목록 (Task 8.6에서 실행)

### shared 모듈
- `commonMain/auth/KakaoAuth.kt` (expect), `androidMain/auth/KakaoAuth.android.kt`·`KakaoAuthHolder.kt`, `iosMain/auth/KakaoAuth.ios.kt`·`KakaoAuthBridge.kt`
- `presentation/login/` 전체 (LoginScreen/ViewModel/State/Event/Dependencies, `KakaoLoginAction`)
- `presentation/onboarding/actions/OnboardingLoginAction.kt`, `KakaoButton` 컴포넌트, `OnboardingState`의 `kakaoId`·`isAuthenticating`
- `data/remote/UserRemoteDataSource.kt` 전체 (UserDto/UserUpsert/KakaoAuthResponse 포함)
- `SupabaseClientProvider`의 `install(Auth)` + `install(Postgrest)` (Functions만 유지)
- `App.kt`의 sessionStatus 분기, `IosApp.kt`의 kakaoBridge 파라미터·iosAuthModule, `androidMain/di`의 androidAuthModule

### androidApp
- `FortuneApp.kt`: `KakaoSdk.init`, `MainActivity.kt`: KakaoAuthHolder 연결
- `AndroidManifest.xml`: `<queries> com.kakao.talk`, `AuthCodeHandlerActivity` + kakao scheme intent-filter
- `build.gradle.kts`: kakao 의존성, `KAKAO_NATIVE_APP_KEY` buildConfigField, manifestPlaceholders

### iosApp
- `KakaoAuthBridgeImpl.swift` 삭제, `iOSApp.swift`의 KakaoSDK import/initSDK/onOpenURL/브리지 인자
- `Info.plist`: kakao URL scheme(CFBundleURLTypes), `LSApplicationQueriesSchemes`의 kakao 항목, `KAKAO_NATIVE_APP_KEY`
- `Secrets.xcconfig`: `KAKAO_NATIVE_APP_KEY`, Xcode SPM `kakao-ios-sdk` 패키지 제거

### Gradle 카탈로그 (`libs.versions.toml`)
- 제거: `kakao`(버전+라이브러리), `firebase`(버전+라이브러리), `supabase-auth`, `supabase-postgrest`
- 유지: `supabase-functions` (fortune 호출), ktor 엔진
- 추가: `datastore-preferences-core` (Task 8.1)

### 백엔드 (Supabase)
- `supabase/functions/kakao-auth/` 디렉토리 삭제 + 원격 함수 삭제 (`supabase functions delete kakao-auth`)
- `users`·`fortunes` 테이블 DROP 마이그레이션 신규 작성 후 원격 적용 (기존 마이그레이션 파일은 이력으로 보존)
- Supabase Auth 기존 사용자 정리 (대시보드)

### 시크릿 (`local.properties`)
- `kakao.nativeAppKey` 제거
- `supabase.secretKey` 제거 — **secrets.md 규칙 위반 상태로 발견됨** (서버 전용 키는 local.properties 저장 금지)

---

## 7. 알려진 한계 (수용)

| 한계 | 내용 | 대응 |
|------|------|------|
| 기기 이전 불가 | 계정이 없으므로 기기 변경·앱 삭제 시 프로필·설정이 소실됨 | 온보딩 재입력 (30초 내). 계정/클라우드 백업은 PRD §12 향후 검토 |
| 재설치 시 운세 재생성 | 같은 날 재설치하면 다른 운세가 나올 수 있음 | v1 수용. 남용 실측 시 서버 캐시 재도입 검토 |
| 알림 시각 오차 | Android inexact alarm은 수 분 오차 가능 | 운세 알림 특성상 수용 |
| Edge Function 공개도 증가 | verify_jwt=false로 apikey만 있으면 호출 가능 | Gemini 무료 티어로 비용 리스크 낮음. 남용 시 rate limit 검토 |
