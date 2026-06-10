# 시크릿 키 관리 규칙

## local.properties 저장 전 체크리스트

민감한 키를 `local.properties`에 저장하기 **전에** 반드시 아래 질문을 확인한다.

### 저장해도 되는 키
- 앱 코드(Kotlin/Swift)에서 직접 사용하는 키
- 예: `supabase.url`, `supabase.publishableKey`

### 저장하면 안 되는 키
- 서버(Edge Function)에서만 사용하는 키
- 외부 대시보드(Supabase 등)에만 입력하는 키
- 예: `supabase.secretKey`(service_role), `GEMINI_API_KEY`

## 판단 기준

> **"이 키가 앱 코드에서 직접 import되거나 사용되는가?"**
>
> - YES → `local.properties`에 저장
> - NO → 해당 서비스 대시보드에만 입력, 저장 안 함

## 키별 저장 위치 정리

| 키 | 저장 위치 |
|----|----------|
| Supabase URL | `local.properties` ✅ |
| Supabase Publishable Key | `local.properties` ✅ |
| Supabase Secret Key (service_role) | Supabase Edge Function 환경변수만 ❌ |
| Gemini API Key | Supabase Edge Function 환경변수만 ❌ |

> **v1.1 (2026-06-10)**: 카카오 로그인·FCM 제거로 Kakao Native App Key / REST API Key / Client Secret / FCM Server Key 항목 삭제. Task 8.6에서 `local.properties`의 `kakao.nativeAppKey`와 `Secrets.xcconfig`의 `KAKAO_NATIVE_APP_KEY`를 제거한다.

## 규칙

- 저장 여부가 불확실하면 사용자에게 먼저 확인한다
- `local.properties`는 `.gitignore`에 등록되어 있어 GitHub에 올라가지 않지만,
  불필요한 키는 최소화하는 것이 원칙이다

## ✅ 위반 사항 해소 이력

- ~~`local.properties`에 `supabase.secretKey`(service_role) 저장~~ — **2026-06-10 Task 8.6에서 제거 완료.** 키가 노출된 이력이 있으므로 Supabase 대시보드에서 롤테이션 권장 (→ `docs/followup-local-migration.md` §1.5). `kakao.nativeAppKey`도 함께 제거됨.
