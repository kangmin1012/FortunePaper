package com.fortune.paper.presentation.settings.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fortune.paper.domain.model.Gender
import com.fortune.paper.presentation.onboarding.components.FPButton
import com.fortune.paper.presentation.onboarding.components.WheelPicker
import com.fortune.paper.presentation.settings.SettingsState
import com.fortune.paper.presentation.theme.FortuneColors

/** 시안(settings.jsx)의 destructive 색. */
private val DestructiveRed = Color(0xFFD14545)

// TODO(배포 전): 플랫폼별 실제 버전명(BuildConfig / CFBundleShortVersionString) 연동
private const val APP_VERSION_LABEL = "FortunePaper · v1.0.0"

/** "HH:mm" → "오전/오후 HH:mm" 표기. */
internal fun notifyTimeLabel(time: String): String {
    val hour = time.substringBefore(":").toIntOrNull() ?: return time
    return if (hour < 12) "오전 $time" else "오후 $time"
}

/** 설정 공통 골격: 상단바(뒤로 ‹ / 중앙 타이틀) + 본문 + 선택적 하단 바. */
@Composable
internal fun SettingsShell(
    title: String,
    onBack: () -> Unit,
    bottomBar: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FortuneColors.bgPrimary)
            .safeContentPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text("‹", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = FortuneColors.textPrimary)
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FortuneColors.textPrimary,
                )
            }
            Spacer(Modifier.width(40.dp))
        }

        Box(Modifier.weight(1f).fillMaxWidth()) { content() }

        bottomBar?.invoke()
    }
}

/** 설정 목록 — 내 정보 / 알림 설정 / 정보 초기화 + 버전 푸터. */
@Composable
internal fun SettingsListContent(
    profileName: String,
    notifyMeta: String,
    onOpenProfile: () -> Unit,
    onOpenNotify: () -> Unit,
    onReset: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = FortuneColors.bgSurface,
            shadowElevation = 2.dp,
        ) {
            Column {
                SettingsRow(label = "내 정보", meta = profileName, onClick = onOpenProfile)
                RowDivider()
                SettingsRow(label = "알림 설정", meta = notifyMeta, onClick = onOpenNotify)
                RowDivider()
                SettingsRow(label = "정보 초기화", destructive = true, onClick = onReset)
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            APP_VERSION_LABEL,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelSmall,
            color = FortuneColors.textTertiary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SettingsRow(
    label: String,
    meta: String? = null,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (destructive) DestructiveRed else FortuneColors.textPrimary,
        )
        if (meta != null) {
            Text(meta, style = MaterialTheme.typography.bodySmall, color = FortuneColors.textTertiary)
        }
        Text("›", fontSize = 18.sp, color = FortuneColors.textTertiary)
    }
}

@Composable
private fun RowDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 0.dp)
            .background(FortuneColors.cream300),
    )
}

/** 알림 설정 편집 — 켜기/끄기 토글 + 시간 프리셋 + 저장하기. */
@Composable
internal fun NotifyEditContent(
    state: SettingsState,
    onToggle: (Boolean) -> Unit,
    onSelectTime: (String) -> Unit,
    onSave: () -> Unit,
) {
    val presets = listOf(
        "06:30" to "이른 아침",
        "07:30" to "출근 전",
        "08:30" to "아침 시간",
        "09:30" to "느긋한 아침",
    )

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(4.dp))
            Text(
                "리포트를 언제 받아 보시겠어요?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FortuneColors.textPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "설정한 시간에 매일 한 번, 오늘의 한 줄 요약을 알려 드립니다.",
                style = MaterialTheme.typography.bodySmall,
                color = FortuneColors.textTertiary,
            )
            Spacer(Modifier.height(16.dp))

            // 알림 토글 — 즉시 적용. 꺼도 시각은 보존된다.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = FortuneColors.bgSurface,
                shadowElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "알림 받기",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = FortuneColors.textPrimary,
                    )
                    Switch(
                        checked = state.notifyEnabled,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(checkedTrackColor = FortuneColors.blue500),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                presets.forEach { (time, label) ->
                    val active = state.draftNotifyTime == time
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable(enabled = state.notifyEnabled) { onSelectTime(time) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FortuneColors.bgSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = if (active) BorderStroke(2.dp, FortuneColors.blue500) else null,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (active) FortuneColors.blue500 else FortuneColors.cream300,
                                        CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("🔔", fontSize = 18.sp)
                            }
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "오전 $time",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FortuneColors.textPrimary,
                                )
                                Text(label, style = MaterialTheme.typography.bodySmall, color = FortuneColors.textTertiary)
                            }
                            if (active) {
                                Text("✓", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FortuneColors.blue500)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Box(Modifier.padding(vertical = 12.dp)) {
            FPButton(text = "저장하기", loading = state.isSaving, onClick = onSave)
        }
    }
}

/** 내 정보 편집 — 이름 / 생년월일 휠 / 성별 카드 / 12시진 그리드 + "잘 모르겠어요". */
@Composable
internal fun ProfileEditContent(
    state: SettingsState,
    onNameChange: (String) -> Unit,
    onBirthChange: (Int, Int, Int) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onBirthTimeChange: (String?) -> Unit,
    onSave: () -> Unit,
) {
    val years = (1950..2010).toList()
    val months = (1..12).toList()
    val days = (1..31).toList()
    val branches = listOf(
        "자" to "23–01시", "축" to "01–03시", "인" to "03–05시", "묘" to "05–07시",
        "진" to "07–09시", "사" to "09–11시", "오" to "11–13시", "미" to "13–15시",
        "신" to "15–17시", "유" to "17–19시", "술" to "19–21시", "해" to "21–23시",
    )

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            // 이름
            FieldLabel("이름")
            OutlinedTextField(
                value = state.draftName,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("예: 김민준", color = FortuneColors.textSecondary) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FortuneColors.blue500,
                    unfocusedBorderColor = FortuneColors.borderDefault,
                    focusedContainerColor = FortuneColors.bgSurface,
                    unfocusedContainerColor = FortuneColors.bgSurface,
                ),
            )
            Spacer(Modifier.height(28.dp))

            // 생년월일
            FieldLabel("생년월일")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FortuneColors.bgSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    WheelPicker(
                        items = years,
                        selectedIndex = years.indexOf(state.draftBirthYear).coerceAtLeast(0),
                        onSelect = { i -> onBirthChange(years[i], state.draftBirthMonth, state.draftBirthDay) },
                        suffix = "년",
                        modifier = Modifier.weight(1.3f),
                    )
                    WheelPicker(
                        items = months,
                        selectedIndex = months.indexOf(state.draftBirthMonth).coerceAtLeast(0),
                        onSelect = { i -> onBirthChange(state.draftBirthYear, months[i], state.draftBirthDay) },
                        suffix = "월",
                        modifier = Modifier.weight(1f),
                    )
                    WheelPicker(
                        items = days,
                        selectedIndex = days.indexOf(state.draftBirthDay).coerceAtLeast(0),
                        onSelect = { i -> onBirthChange(state.draftBirthYear, state.draftBirthMonth, days[i]) },
                        suffix = "일",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(28.dp))

            // 성별
            FieldLabel("성별")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GenderEditCard(
                    modifier = Modifier.weight(1f),
                    label = "여성",
                    mark = "음",
                    accent = Color(0xFFE8B4D2),
                    active = state.draftGender == Gender.FEMALE,
                    onClick = { onGenderChange(Gender.FEMALE) },
                )
                GenderEditCard(
                    modifier = Modifier.weight(1f),
                    label = "남성",
                    mark = "양",
                    accent = Color(0xFFFFD27A),
                    active = state.draftGender == Gender.MALE,
                    onClick = { onGenderChange(Gender.MALE) },
                )
            }
            Spacer(Modifier.height(28.dp))

            // 태어난 시각 (선택)
            FieldLabel("태어난 시각", optional = true)
            branches.chunked(4).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowItems.forEach { (name, range) ->
                        val active = state.draftBirthTime == name
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(
                                    if (active) FortuneColors.blue500 else FortuneColors.bgSurface,
                                    RoundedCornerShape(12.dp),
                                )
                                .clickable { onBirthTimeChange(name) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${name}시",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) FortuneColors.white else FortuneColors.textPrimary,
                                )
                                Text(
                                    range,
                                    fontSize = 9.sp,
                                    color = if (active) FortuneColors.white.copy(alpha = 0.8f) else FortuneColors.textTertiary,
                                )
                            }
                        }
                    }
                }
            }
            val unknownActive = state.draftBirthTime == null
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable { onBirthTimeChange(null) }
                    .let { m ->
                        if (unknownActive) {
                            m.background(FortuneColors.blue500.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        } else m
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "잘 모르겠어요",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (unknownActive) FortuneColors.blue500 else FortuneColors.textTertiary,
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(FortuneColors.cream300),
            )
            Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                FPButton(
                    text = "저장하기",
                    enabled = state.canSaveProfile,
                    loading = state.isSaving,
                    onClick = onSave,
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String, optional: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = FortuneColors.textTertiary,
        )
        if (optional) {
            Spacer(Modifier.width(6.dp))
            Text("선택", style = MaterialTheme.typography.labelSmall, color = FortuneColors.textTertiary)
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun GenderEditCard(
    modifier: Modifier,
    label: String,
    mark: String,
    accent: Color,
    active: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FortuneColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (active) 4.dp else 1.dp),
        border = if (active) BorderStroke(2.dp, FortuneColors.blue500) else null,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(mark, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0x80000000))
            }
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FortuneColors.textPrimary,
            )
        }
    }
}

/** iOS 스타일 확인 다이얼로그 (시안 ConfirmDialog 기준). */
@Composable
internal fun ResetConfirmDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = FortuneColors.bgSurface,
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "정보를 초기화할까요?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FortuneColors.textPrimary,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "이름·생년월일 등 입력한 모든 정보와 알림 설정이 삭제되고 처음 화면으로 돌아갑니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FortuneColors.textTertiary,
                        textAlign = TextAlign.Center,
                    )
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(FortuneColors.cream300))
                Row {
                    Box(
                        modifier = Modifier.weight(1f).clickable(onClick = onCancel).padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("취소", style = MaterialTheme.typography.bodyLarge, color = FortuneColors.textPrimary)
                    }
                    Box(Modifier.width(1.dp).height(46.dp).background(FortuneColors.cream300))
                    Box(
                        modifier = Modifier.weight(1f).clickable(onClick = onConfirm).padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "초기화",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = DestructiveRed,
                        )
                    }
                }
            }
        }
    }
}

/** 하단 탭바 — 오늘/설정 (설정 활성). */
@Composable
internal fun SettingsTabBar(onTabToday: () -> Unit) {
    Column {
        Box(Modifier.fillMaxWidth().height(1.dp).background(FortuneColors.cream300))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FortuneColors.bgSurface)
                .padding(top = 8.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsTabItem(Modifier.weight(1f), emoji = "🏠", label = "오늘", active = false, onClick = onTabToday)
            SettingsTabItem(Modifier.weight(1f), emoji = "⚙️", label = "설정", active = true, onClick = {})
        }
    }
}

@Composable
private fun SettingsTabItem(
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
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) FortuneColors.blue500 else FortuneColors.textSecondary,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}
