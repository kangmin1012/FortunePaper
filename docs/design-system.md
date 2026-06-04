# 디자인 시스템 (코드 매핑)

> 출처: `design/FortunePaper_Design/design-system/colors_and_type.css` · `components.jsx`
> 원칙(ui.md): 값 하드코딩 금지 — 아래 토큰을 통해 `MaterialTheme` / `Fortune*` 토큰을 사용한다.
> 위치: `shared/src/commonMain/.../presentation/theme/`

---

## 1. 컬러 — `FortuneColors.kt`

CSS `:root` 변수를 1:1로 옮긴 값. `MaterialTheme.colorScheme`(`Theme.kt`의 `FortuneColorScheme`)에 매핑되지 않는 추가 토큰(크림 배경 단계, 날씨 등급 색)을 노출.

| 그룹 | 토큰 |
|------|------|
| Neutral | `cream100/200/300`, `gray200~900`, `white` |
| Brand | `blue400/500/600` |
| Semantic | `bgPrimary(=cream200)`, `bgSurface(=white)`, `textPrimary/Secondary/Tertiary`, `borderDefault`, `error` |
| Kakao | `kakaoYellow(#FEE500)`, `kakaoLabel` |
| Grade(color) | `gradeSunny/Clear/Cloudy/Rainy/Storm` — 아이콘·글로우 |
| Grade(headline) | `grade*Headline` — 흰 카드 위 등급 키워드 텍스트(진한 변형) |

### MaterialTheme.colorScheme 매핑 (`Theme.kt`)
`primary=blue500`, `background=bgPrimary`, `surface=bgSurface`, `surfaceVariant=cream300`, `outline=borderDefault`, `error=error` …

### 날씨 등급 매퍼 — `GradeColors.kt`
- `FortuneGrade.color()` → 아이콘/글로우 색
- `FortuneGrade.headlineColor()` → 카드 위 키워드 텍스트 색

| 등급 | color | headline |
|------|-------|----------|
| SUNNY | #FFD700 | #C99000 |
| CLEAR | #87CEEB | #2E7DA8 |
| CLOUDY | #B0BEC5 | #607883 |
| RAINY | #5C8AC8 | #3F6DAC |
| STORM | #546E7A | #37474F |

---

## 2. 타이포 — `Theme.kt` + `FortuneType.kt`

- 폰트: **Noto Sans KR** 9웨이트(`composeResources/font/notosans_kr_*`). `Theme.kt`의 `notoSansKr()` → `Typography`의 모든 슬롯에 패밀리 적용.
- 스케일 토큰: `FortuneType` (sp/weight/line-height). 화면에서는 `MaterialTheme.typography.<slot>.copy(fontSize = FortuneType.xxx)` 형태로 써서 **폰트 패밀리(Noto)를 유지하면서 디자인 스케일**을 입힌다.

| 토큰 | 값 | 용도 |
|------|-----|------|
| `headingXl` | 32sp | 등급 HERO |
| `gradeName` | 30sp | revealed 등급 키워드 |
| `headingLg` | 24sp | 화면 제목 |
| `headingMd` | 20sp | 섹션 제목 |
| `bodyLg` | 16sp | 한 줄 요약 |
| `bodyMd` | 14sp | 기본 본문 |
| `bodySm` | 13sp | 보조 본문 |
| `caption` | 11sp | 날짜·메타 |
| line-height | `lhTight/Snug/Normal/Loose` = 1.2/1.35/1.5/1.7 | |

---

## 3. 간격·반경·모양 — `FortuneDimens.kt` + `FortuneShapes.kt`

- `FortuneSpacing`: xs4 / sm8 / md12 / lg16 / xl24 / xxl48
- `FortuneRadius`: sm8 / md16 / lg20 / full999
- `FortuneShapes`(→ `MaterialTheme.shapes`): small=8, medium=16, large=20

## 4. 그림자

CSS `--shadow-card`는 Compose에서 `Surface(shadowElevation = 2.dp)`로 근사(카드). 별도 Modifier 미도입.

---

## 5. 적용 현황

- ✅ 온보딩(`presentation/onboarding/components`) — StepShell·FPButton·WheelPicker·GradeStrip(이모지)
- ✅ 리포트(`presentation/report/components`) — HomeShell·등급 hero·요약/조언 카드·로딩/오류
- 날씨 아이콘은 SVG 대신 **이모지**로 통일 (사유·향후 계획은 `docs/deferred-resources.md` 참고)
