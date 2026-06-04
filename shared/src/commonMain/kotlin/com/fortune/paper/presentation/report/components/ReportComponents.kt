package com.fortune.paper.presentation.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortune.paper.domain.model.FortuneReport
import com.fortune.paper.presentation.onboarding.components.FPButton
import com.fortune.paper.presentation.onboarding.components.GradeStrip
import com.fortune.paper.presentation.theme.FortuneColors
import com.fortune.paper.presentation.theme.FortuneRadius
import com.fortune.paper.presentation.theme.FortuneSpacing
import com.fortune.paper.presentation.theme.FortuneType
import com.fortune.paper.presentation.theme.color
import com.fortune.paper.presentation.theme.headlineColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

/** "MM.DD · 요일" 라벨. 파싱 실패 시 원본 문자열 반환. */
internal fun reportDateLabel(dateIso: String): String = runCatching {
    // dateIso 는 ISO "YYYY-MM-DD" — 월/일은 문자열에서 직접 취해 deprecated API를 피한다.
    val parts = dateIso.split("-")
    val mm = parts[1]
    val dd = parts[2]
    val dow = when (LocalDate.parse(dateIso).dayOfWeek.isoDayNumber) {
        1 -> "월요일"; 2 -> "화요일"; 3 -> "수요일"; 4 -> "목요일"
        5 -> "금요일"; 6 -> "토요일"; else -> "일요일"
    }
    "$mm.$dd · $dow"
}.getOrDefault(dateIso)

/**
 * 홈 셸: 상단 내비(제목 + 새로고침) + 본문 + 하단 탭바(오늘/설정).
 * 세 상태(로딩/리포트/오류) 내내 유지되어 일관된 홈 컨텍스트를 준다.
 */
@Composable
fun ReportHomeShell(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onTabSettings: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FortuneColors.bgPrimary)
            .safeContentPadding(),
    ) {
        // nav bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = FortuneSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(40.dp))
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    "오늘의 리포트",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FortuneType.semibold,
                    color = FortuneColors.textPrimary,
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(enabled = !isRefreshing, onClick = onRefresh),
                contentAlignment = Alignment.Center,
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = FortuneColors.blue500,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("↻", fontSize = 22.sp, fontWeight = FortuneType.bold, color = FortuneColors.textPrimary)
                }
            }
        }

        Box(Modifier.weight(1f).fillMaxWidth()) { content() }

        ReportTabBar(onTabSettings = onTabSettings)
    }
}

@Composable
private fun ReportTabBar(onTabSettings: () -> Unit) {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(FortuneColors.cream300),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FortuneColors.bgSurface)
                .padding(top = FortuneSpacing.sm, bottom = 18.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TabItem(Modifier.weight(1f), emoji = "🏠", label = "오늘", active = true, onClick = {})
            TabItem(Modifier.weight(1f), emoji = "⚙️", label = "설정", active = false, onClick = onTabSettings)
        }
    }
}

@Composable
private fun TabItem(
    modifier: Modifier,
    emoji: String,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(FortuneSpacing.xs))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) FortuneColors.blue500 else FortuneColors.textSecondary,
            fontWeight = if (active) FortuneType.semibold else FortuneType.medium,
        )
    }
}

/** 1. 로딩(계산 중) 상태. */
@Composable
fun LoadingReport() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = FortuneSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GradeStrip()
        Spacer(Modifier.height(FortuneSpacing.xl))
        CircularProgressIndicator(color = FortuneColors.blue500)
        Spacer(Modifier.height(FortuneSpacing.lg))
        Text(
            "오늘의 흐름을 살피는 중…",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = FortuneType.bodySm,
            color = FortuneColors.textTertiary,
        )
    }
}

/** 2. 오류 상태 + 재시도. */
@Composable
fun ErrorReport(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("⚠️", fontSize = 52.sp)
        Spacer(Modifier.height(FortuneSpacing.lg))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = FortuneType.bodyMd,
            color = FortuneColors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(FortuneSpacing.xl))
        FPButton(text = "다시 시도", onClick = onRetry)
    }
}

/** 3. 공개(오늘의 리포트) 상태. */
@Composable
fun ReportRevealed(report: FortuneReport) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = FortuneSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(FortuneSpacing.sm))
        Text(
            reportDateLabel(report.date),
            style = MaterialTheme.typography.labelMedium,
            fontSize = FortuneType.caption,
            color = FortuneColors.textTertiary,
        )
        Spacer(Modifier.height(FortuneSpacing.sm))

        // hero
        Text(report.grade.icon, fontSize = 96.sp)
        Spacer(Modifier.height(FortuneSpacing.xs))
        Text(
            report.grade.displayName,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = FortuneType.gradeName,
            fontWeight = FortuneType.extraBold,
            color = report.grade.headlineColor(),
        )

        Spacer(Modifier.height(FortuneSpacing.lg))
        SummaryCard(report)
        Spacer(Modifier.height(FortuneSpacing.md))
        AdviceCard(report)
        Spacer(Modifier.height(FortuneSpacing.xl))
    }
}

/** 한 줄 요약 카드. */
@Composable
private fun SummaryCard(report: FortuneReport) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = FortuneColors.bgSurface,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = FortuneSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FortuneSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(report.grade.color().copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("✓", color = report.grade.headlineColor(), fontSize = 14.sp, fontWeight = FortuneType.bold)
            }
            Text(
                report.summary,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = FortuneType.bodyMd,
                fontWeight = FortuneType.semibold,
                color = FortuneColors.textPrimary,
            )
        }
    }
}

/** 오늘의 조언 카드. */
@Composable
private fun AdviceCard(report: FortuneReport) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = FortuneColors.bgSurface,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(FortuneSpacing.lg)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FortuneSpacing.sm),
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(14.dp)
                        .background(report.grade.headlineColor(), RoundedCornerShape(2.dp)),
                )
                Text(
                    "오늘의 조언",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = FortuneType.bodySm,
                    fontWeight = FortuneType.bold,
                    color = FortuneColors.textPrimary,
                )
            }
            Spacer(Modifier.height(FortuneSpacing.md))
            Text(
                report.advice,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = FortuneType.bodyMd,
                lineHeight = (FortuneType.bodyMd.value * FortuneType.lhLoose).sp,
                color = FortuneColors.textPrimary,
            )
        }
    }
}
