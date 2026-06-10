# 후속 작업 — v1.1 로컬 전환 마이그레이션 (Task 8) 잔여 항목

> **작성**: 2026-06-10 | 관련: [`docs/spec-local-user-migration.md`](spec-local-user-migration.md) · [`.claude/rules/tasks.md`](../.claude/rules/tasks.md)
>
> Task 8 시리즈의 **코드 작업은 전부 완료**되었고 Android(`assembleDebug`)·iOS(시뮬레이터) 빌드 검증을 통과했다.
> 아래는 이 세션에서 수행할 수 없었던 **원격(프로덕션) 작업**과 보류된 보완 항목이다.

---

## 1. 원격(Supabase) 작업 — 수동 수행 필요

이 세션에서는 프로덕션 배포 권한이 차단되어 로컬 코드 작업까지만 완료했다. 아래 명령을 직접 실행해야 한다.

### 1.1 fortune Edge Function 배포 (stateless 개편본)

```bash
supabase functions deploy fortune --project-ref zvqecylagvkznetltlpu --no-verify-jwt
```

- `supabase/config.toml`에 `[functions.fortune] verify_jwt = false`가 이미 설정되어 있으므로 `--no-verify-jwt` 플래그 없이도 동일하게 동작한다.
- **배포 후 스모크 테스트**:
  ```bash
  URL="https://zvqecylagvkznetltlpu.supabase.co/functions/v1/fortune"
  ANON="sb_publishable_..."   # local.properties의 supabase.publishableKey

  # ① apikey 포함 → 200 + { date, grade, summary, advice }
  curl -s -X POST "$URL" -H "apikey: $ANON" -H "Content-Type: application/json" \
    -d '{"birth_date":"1995-01-01","gender":"MALE","birth_time":"자"}'

  # ② apikey 미포함 → 401 (게이트웨이 차단)
  curl -s -o /dev/null -w "%{http_code}" -X POST "$URL" \
    -H "Content-Type: application/json" -d '{}'
  ```

### 1.2 kakao-auth 원격 함수 삭제

```bash
supabase functions delete kakao-auth --project-ref zvqecylagvkznetltlpu
```

(로컬 `supabase/functions/kakao-auth/` 디렉토리는 이미 삭제됨)

### 1.3 users·fortunes 테이블 DROP

마이그레이션 파일 작성 완료: `supabase/migrations/20260610120000_drop_users_and_fortunes.sql`

- 적용 방법 ①: Supabase 대시보드 SQL Editor에서 해당 SQL 실행 (기존 마이그레이션과 동일 방식)
- 적용 방법 ②: `supabase link` 후 `supabase db push`

### 1.4 Supabase Auth 기존 사용자 정리

대시보드 → Authentication → Users에서 기존 카카오 연동 사용자 삭제.

### 1.5 Secret Key 롤테이션 (보안)

`local.properties`에 저장돼 있던 `supabase.secretKey`(service_role)는 제거 완료.
**키가 로컬 파일에 노출된 이력이 있으므로** 대시보드 → Settings → API에서 롤테이션을 권장한다.
(현재 남은 서버 코드(fortune)는 service_role을 사용하지 않으므로 롤테이션해도 영향 없음)

---

## 2. 배포 후 검증 (Task 8.5 이관 항목)

- [ ] **실제 Gemini 생성 e2e** — 1.1 배포 후 앱에서 온보딩 → 리포트 화면 진입 → 실 운세 생성 확인
  (Task 4부터 이월된 항목. stateless 개편본 기준으로 검증해야 한다)

---

## 3. 코드 내 TODO / 보완 항목

| 위치 | 내용 |
|------|------|
| `presentation/settings/components/SettingsComponents.kt` | `APP_VERSION_LABEL`이 `"FortunePaper · v1.0.0"` 하드코딩 (시안 문구 기준). 배포 전 플랫폼별 실제 버전(BuildConfig.versionName / CFBundleShortVersionString) 연동 필요 |
| 앱 아이콘 | 시안만 존재(`App Icon.html` / `App Icon - Android.html`). 실제 리소스(adaptive icon / AppIcon asset) 적용은 Task 7(배포)에서 수행 |
| 알림 아이콘 (Android) | `showDailyNotification`이 `context.applicationInfo.icon`(런처 아이콘)을 smallIcon으로 사용. 전용 흑백 status bar 아이콘 제작 권장 (선택) |
| 디자인 보류 리소스 | 기존 `docs/deferred-resources.md` 참고 (SVG 등급 아이콘 등) |

---

## 4. 기타 결정 기록

- **`supabase/functions/ping-db/`는 유지** — Free 플랜 일시정지 방지 heartbeat 용도로 users/fortunes 테이블을 사용하지 않는다. 단, DB의 `ping` RPC 함수에 의존하므로 테이블 DROP과 무관하게 동작한다. 불필요해지면 별도 삭제.
- **iosApp의 SPM `Package.resolved`** — 카카오 핀이 남아 있어 삭제했다. Xcode에서 프로젝트를 열면 (kakao 패키지 참조가 pbxproj에서 제거된 상태로) 자동 재생성된다.
- **카카오 개발자 콘솔** — 등록된 앱(네이티브 키 `37043b...`)은 더 이상 사용하지 않으므로 비활성화/삭제 가능 (선택).
