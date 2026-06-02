# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## 프로젝트 개요

**포춘페이퍼 (FortunePaper)** — 사주(생년월일 + 성별) 기반 운세를 한 장의 리포트 형식으로 매일 발행하는 KMP 모바일 앱.
패키지명: `com.fortune.paper`

---

## 빌드 및 실행 명령어

**Java 설정 (Android Studio JDK 사용)**
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

**Android**
```bash
./gradlew :androidApp:assembleDebug        # 디버그 APK 빌드
./gradlew :androidApp:assembleRelease      # 릴리즈 APK 빌드
./gradlew :androidApp:installDebug         # 연결된 기기에 설치
```

**테스트**
```bash
./gradlew :shared:testAndroidHostTest      # Android 공통 로직 테스트
./gradlew :shared:iosSimulatorArm64Test    # iOS 시뮬레이터 테스트
```

**iOS**: `iosApp/` 디렉토리를 Xcode에서 열고 실행.

> `local.properties`의 `sdk.dir=/Users/kangmingu/Library/Android/sdk` 설정 필요.

---

## Rules (자동 로드)

`.claude/rules/` 파일은 세션 시작 시 자동으로 로드됩니다.

| 파일 | 내용 |
|------|------|
| `prd.md` | 제품 요구사항 — 등급 체계, 기능 명세, 페르소나, 성공 지표 |
| `tasks.md` | 개발 태스크 체크리스트 — 단계별 구현 항목 |
| `architecture.md` | Clean Architecture · MVI · TOAD 패턴, 레이어 구조, DB 스키마, 백엔드 흐름 |
| `di.md` | Koin 모듈 구성 및 의존성 주입 규칙 |
| `ui.md` | Composable 규칙, Material3 디자인 시스템, Coil 이미지 로딩 |
| `secrets.md` | 민감한 키 저장 규칙 — local.properties 저장 전 체크리스트 |

---

## 작업 규칙

- **Task 개발이 완료되면 항상 `.claude/rules/tasks.md` 문서를 업데이트한다.** 완료한 항목의 체크박스를 `[x]`(또는 상태 표기 `✅`)로 갱신하고, 실제 구현 상태와 문서가 어긋나지 않도록 유지한다.

---

## 디자인

UI 개발 시 **`design/FortunePaper_Design/` 폴더를 디자인 기준으로 참고하여 개발한다.** 이 폴더는 포춘페이퍼 앱의 디자인 시스템과 실제 화면 시안을 담고 있다.

| 경로 | 내용 |
|------|------|
| `design-system/colors_and_type.css` | 디자인 토큰 — 컬러(neutral·brand·날씨 등급), 타이포 스케일, spacing/radius/shadow/motion. `MaterialTheme` 토큰 매핑의 기준 |
| `design-system/components.jsx` | 공통 컴포넌트 시안 |
| `design-system/assets/grade-*.svg` | 날씨 5단계 등급 아이콘 (SUNNY/CLEAR/CLOUDY/RAINY/STORM) |
| `Onboarding.html`, `screens.jsx` | 온보딩 플로우 화면 시안 |
| `Daily Entry.html`, `daily-entry.jsx` | 리포트 메인 화면 시안 |
| `Settings.html`, `settings.jsx` | 설정 화면 시안 |

- `ui.md`의 "Material3 토큰 사용, 값 하드코딩 금지" 원칙을 유지하되, 토큰 값은 위 `colors_and_type.css`를 출처로 삼아 `MaterialTheme`에 매핑한다.
- 화면 구현 전 해당 시안 파일(`*.html` / `*.jsx`)을 확인하고, 시안과 어긋나는 결정이 필요하면 사용자에게 먼저 확인한다.
