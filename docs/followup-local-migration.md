# 후속 작업 — v1.1 로컬 전환 마이그레이션 (Task 8) 잔여 항목

> **작성**: 2026-06-10 | 관련: [`docs/spec-local-user-migration.md`](spec-local-user-migration.md) · [`.claude/rules/tasks.md`](../.claude/rules/tasks.md)
>
> Task 8 시리즈의 **코드 작업은 전부 완료**되었고 Android(`assembleDebug`)·iOS(시뮬레이터) 빌드 검증을 통과했다.
> 아래는 이 세션에서 수행할 수 없었던 **원격(프로덕션) 작업**과 보류된 보완 항목이다.

---

## 1. 원격(Supabase) 작업

> **✅ 1.1~1.5 전부 완료** — 1.1·1.2·1.3 (2026-06-14), 1.4·1.5 (2026-06-27, 대시보드 수동 처리).

### 1.1 fortune Edge Function 배포 (stateless 개편본) — ✅ 완료 (2026-06-14)

```bash
supabase functions deploy fortune --project-ref zvqecylagvkznetltlpu
```

- `supabase/config.toml`에 `[functions.fortune] verify_jwt = false`가 이미 설정되어 있어 `--no-verify-jwt` 없이 배포됨. 현재 ACTIVE v3.
- **모델 교체**: 배포 후 첫 호출에서 `gemini-1.5-flash`가 404(구글 retire)로 실패 → `gemini-2.5-flash`로 교체 후 재배포하여 정상 생성 확인.
- **스모크 테스트 결과**:
  ```bash
  URL="https://zvqecylagvkznetltlpu.supabase.co/functions/v1/fortune"
  ANON="sb_publishable_..."   # local.properties의 supabase.publishableKey
  curl -s -X POST "$URL" -H "apikey: $ANON" -H "Content-Type: application/json" \
    -d '{"birth_date":"1995-01-01","gender":"MALE","birth_time":"자"}'
  # → 200 { "date":"2026-06-14","grade":"CLEAR","summary":"...","advice":"..." }  (birth_time null 도 정상)
  ```
- ⚠️ **보안 발견**: `verify_jwt = false`는 Supabase 게이트웨이의 **apikey 검사까지 비활성화**한다. apikey 없이 보낸 요청도 함수에 도달(우리 함수의 400 검증 응답 반환)하므로, fortune 엔드포인트는 사실상 **공개 무인증** 상태다. architecture.md/spec의 "게이트웨이 apikey 검사는 유지" 서술은 사실과 다르며 정정함. PRD §12("Gemini 호출 남용 실측 시 rate limit/디바이스 ID 캐시 검토")가 이 위험을 이미 수용 항목으로 다룬다 — 출시 후 남용 모니터링 필요.

### 1.2 kakao-auth 원격 함수 삭제 — ✅ 완료 (이미 부재 확인)

```bash
supabase functions delete kakao-auth --project-ref zvqecylagvkznetltlpu
# → "Function kakao-auth does not exist ... nothing to delete" (원격에 이미 없음)
```

원격 함수 목록은 `fortune`(ACTIVE v3)·`ping-db`만 잔존 — 정상.

### 1.3 users·fortunes 테이블 DROP — ✅ 완료 (2026-06-14)

마이그레이션 `supabase/migrations/20260610120000_drop_users_and_fortunes.sql`을 `supabase db push --linked`로 원격 적용. 마이그레이션 히스토리에 `20260610120000` 기록 확인, "Remote database is up to date".

### 1.4 Supabase Auth 기존 사용자 정리 — ✅ 완료 (2026-06-27)

대시보드 → Authentication → Users에서 기존 카카오 연동 사용자 삭제 완료.

### 1.5 Secret Key 롤테이션 (보안) — ✅ 완료 (2026-06-27)

`local.properties`에 저장돼 있던 `supabase.secretKey`(service_role)는 제거 완료.
키가 로컬 파일에 노출된 이력이 있어 대시보드 → Settings → API에서 롤테이션 완료.
(남은 서버 코드(fortune)는 service_role을 사용하지 않아 롤테이션 영향 없음)

---

## 2. 배포 후 검증 (Task 8.5 이관 항목)

- [x] **Gemini 생성 e2e (서버 레벨)** — 1.1 배포본에 직접 curl로 실 운세 생성 확인 (birth_time 유/무 모두 200 + 정상 JSON, 2026-06-14).
- [ ] **앱 레벨 e2e (선택)** — 실기기/시뮬레이터에서 온보딩 → 리포트 화면 진입 → 실 생성·캐시 저장까지 확인. 서버는 검증됐으므로 앱 통합 경로 최종 확인용.
  - 참고: 함수가 `summary`를 40자, `advice`를 80자로 클램프하지만 프롬프트는 20/50자를 요구한다. Gemini가 종종 50자를 초과(예: advice ~60자) → 리포트 카드 레이아웃에서 길이 확인 권장. 엄격 준수 필요 시 프롬프트 강화 또는 클램프 하향.

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

- **레포 잔재 정리 완료 (2026-06-27)** — 빌드와 무관하게 남아있던 카카오 흔적 제거 + `assembleDebug` 빌드 검증(APK 생성 확인):
  - `settings.gradle.kts`의 카카오 maven 저장소(`devrepo.kakao.com`) 제거
  - 미사용 에셋 `shared/src/commonMain/composeResources/drawable/kakao_login_medium_wide.png` 삭제 (참조 코드 없음)
  - `iosApp/Configuration/Secrets.xcconfig.example`의 `KAKAO_NATIVE_APP_KEY` 라인 제거
  - (보류) `design/FortunePaper_Design/screens.jsx`에 미사용 `KakaoLoginButton`/`KakaoSymbol` 정의 잔존 — 디자인 시안 소스라 별도 정리 대상
- **`supabase/functions/ping-db/`는 유지** — Free 플랜 일시정지 방지 heartbeat 용도로 users/fortunes 테이블을 사용하지 않는다. 단, DB의 `ping` RPC 함수에 의존하므로 테이블 DROP과 무관하게 동작한다. 불필요해지면 별도 삭제.
- **iosApp의 SPM `Package.resolved`** — 카카오 핀이 남아 있어 삭제했다. Xcode에서 프로젝트를 열면 (kakao 패키지 참조가 pbxproj에서 제거된 상태로) 자동 재생성된다.
- **카카오 개발자 콘솔** — 등록된 앱(네이티브 키 `37043b...`)은 더 이상 사용하지 않아 비활성화/삭제 완료 (2026-06-27).
