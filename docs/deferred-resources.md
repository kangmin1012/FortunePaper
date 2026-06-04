# 보류·누락 리소스 기록

> 자율 진행 중 누락/미적용으로 판단해 **일단 넘어간** 항목들. 작업 중단 없이 기록만 남긴다.
> 각 항목은 향후 별도 태스크로 처리.

## Task 5 (리포트 화면 UI) — 2026-06-04

### 1. 날씨 등급 SVG 아이콘 (보류 → 이모지 대체)
- 자산 존재: `design/FortunePaper_Design/design-system/assets/grade-{sunny,clear,cloudy,rainy,storm}.svg`
- 보류 사유: Compose Multiplatform `Res.drawable`은 SVG를 직접 지원하지 않음(벡터 XML/PNG만). 온보딩에서 이미 이모지로 대체한 선례가 있어 일관성 위해 이모지(`FortuneGrade.icon`) 사용.
- 향후: ① SVG→ImageVector(XML) 변환 후 `Res.drawable` 등록, 또는 ② Coil + SVG 디코더로 로드. hero/요약/계산 화면의 시각 완성도 상향.

### 2. 하단 탭바 아이콘 (이모지 대체)
- 시안은 stroke 기반 SVG(홈/기어). 현재 `🏠`/`⚙️` 이모지로 대체.
- 향후: 위 1번과 함께 벡터 아이콘 세트로 교체.

### 3. Awaiting/Calculating 정교한 연출 (간소화)
- 시안: `PaperSeal`(밀봉 종이+왁스실), 회전하는 5등급 아이콘, 단계별 문구 애니메이션.
- 현재: 로딩 = `GradeStrip`(이모지) + 스피너 + "오늘의 흐름을 살피는 중…" 문구. "봉인→공개" 단계 플로우는 미구현(로딩→공개 단순화).
- 향후: 데이터 흐름(캐시 즉시/생성 대기) 구분이 명확해지면 awaiting 진입 화면 추가 검토.

### 4. 인사말/스트릭/리치 본문 (v1 데이터 범위 밖)
- 시안의 `안녕하세요 {name}님`, `N일 연속`, 4문단 본문(`FORTUNE_BODY`)은 v1 데이터 모델(summary 20자 + advice 50자) 범위를 초과.
- 현재: 등급 + 한 줄 요약 + 조언만 렌더(PRD §4.3 준수). 스트릭/인사말 생략.
- 향후: 데이터 모델 확장(v2) 시 검토.

### 5. 설정 화면 네비게이션
- `ReportScreen(onNavigateToSettings)` + 탭바 "설정"은 Task 6에서 실제 화면으로 연결 예정. 현재 no-op.
