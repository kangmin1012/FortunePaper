package com.fortune.paper.presentation.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fortune.paper.presentation.theme.FortuneColors

/** 7단계 진행 바 (얇은 바, 디자인의 ProgressDots 대체). */
@Composable
fun OnboardingProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(4.dp)
            .background(FortuneColors.cream300, RoundedCornerShape(999.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(FortuneColors.blue500, RoundedCornerShape(999.dp))
        )
    }
}

/**
 * 단계 공통 골격: 상단바(뒤로/진행바/건너뛰기) + 본문 + 하단 CTA.
 */
@Composable
fun StepShell(
    progress: Float,
    showProgress: Boolean = true,
    onBack: (() -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FortuneColors.bgPrimary)
            .safeContentPadding()
    ) {
        // top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("‹", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = FortuneColors.textPrimary)
                }
            } else {
                Spacer(Modifier.width(40.dp))
            }

            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (showProgress) OnboardingProgressBar(progress)
            }

            if (onSkip != null) {
                TextButton(onClick = onSkip) {
                    Text(
                        "건너뛰기",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FortuneColors.textTertiary,
                    )
                }
            } else {
                Spacer(Modifier.width(40.dp))
            }
        }

        // body
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            content()
        }

        // footer CTA
        if (footer != null) {
            Box(Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 28.dp)) {
                footer()
            }
        }
    }
}

/** 기본 CTA 버튼 (full-width, 56dp). */
@Composable
fun FPButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FortuneColors.blue500,
            contentColor = FortuneColors.white,
            disabledContainerColor = FortuneColors.gray300,
            disabledContentColor = FortuneColors.white,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = FortuneColors.white,
                strokeWidth = 2.dp,
            )
        } else {
            Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

/** 단계 상단의 작은 라벨 (eyebrow). */
@Composable
fun Eyebrow(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = FortuneColors.textTertiary,
        fontWeight = FontWeight.Medium,
    )
}

/** 단계 제목. */
@Composable
fun StepTitle(text: String) {
    Text(
        text = text,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold,
        color = FortuneColors.textPrimary,
    )
}

/** 단계 보조 설명. */
@Composable
fun StepSubtitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = FortuneColors.textTertiary,
    )
}

/**
 * 간단한 휠 피커. 가운데 선택 밴드에 현재 값이 오도록 정렬하며,
 * 보이는 항목을 탭하면 해당 값으로 스크롤·선택된다.
 */
@Composable
fun WheelPicker(
    items: List<Int>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    suffix: String,
    modifier: Modifier = Modifier,
) {
    val itemHeight = 44.dp
    val visibleCount = 5
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex.coerceAtLeast(0))
    }

    Box(
        modifier = modifier.height(itemHeight * visibleCount),
        contentAlignment = Alignment.Center,
    ) {
        // 선택 밴드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(FortuneColors.cream200, RoundedCornerShape(10.dp))
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            // 첫/마지막 항목이 가운데 밴드까지 올 수 있도록 위아래 패딩
            contentPadding = PaddingValues(vertical = itemHeight * 2),
        ) {
            itemsIndexed(items) { index, value ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$value$suffix",
                        fontSize = if (selected) 20.sp else 17.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) FortuneColors.textPrimary else FortuneColors.textTertiary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/** 날씨 등급 이모지 + 한글명 (디자인 SVG 아이콘 대체). */
data class GradeVisual(val emoji: String, val label: String)

val GRADE_VISUALS: List<GradeVisual> = listOf(
    GradeVisual("☀️", "화창"),
    GradeVisual("🌤️", "맑음"),
    GradeVisual("☁️", "구름"),
    GradeVisual("🌧️", "비"),
    GradeVisual("⛈️", "폭풍"),
)

@Composable
fun GradeStrip(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GRADE_VISUALS.forEach { g ->
            Text(g.emoji, fontSize = 26.sp)
        }
    }
}

/** 큰 히어로 이모지 (Welcome 화면용). */
@Composable
fun HeroGradeIcon(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(160.dp), contentAlignment = Alignment.Center) {
        Text("☀️", fontSize = 120.sp)
    }
}

/** 둥근 점/심볼 표시용 (성별 카드 음양 등). */
@Composable
fun CircleBadge(
    text: String,
    background: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(56.dp).background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color(0x80000000))
    }
}
