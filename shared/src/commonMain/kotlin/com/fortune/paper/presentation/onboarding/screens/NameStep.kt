package com.fortune.paper.presentation.onboarding.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.fortune.paper.presentation.onboarding.OnboardingState
import com.fortune.paper.presentation.onboarding.components.Eyebrow
import com.fortune.paper.presentation.onboarding.components.StepSubtitle
import com.fortune.paper.presentation.onboarding.components.StepTitle
import com.fortune.paper.presentation.theme.FortuneColors

@Composable
fun NameStep(
    state: OnboardingState,
    onNameChange: (String) -> Unit,
) {
    Column {
        Spacer(Modifier.height(12.dp))
        Eyebrow("1 / 4 — 이름")
        Spacer(Modifier.height(12.dp))
        StepTitle("어떻게 불러\n드릴까요?")
        Spacer(Modifier.height(10.dp))
        StepSubtitle("리포트 인사말과 알림에서만 사용됩니다.")
        Spacer(Modifier.height(28.dp))

        Text("이름", style = MaterialTheme.typography.bodySmall, color = FortuneColors.textSecondary)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = state.name,
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
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Done),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "한글 · 영문 · 숫자, 최대 ${OnboardingState.MAX_NAME_LENGTH}자",
            style = MaterialTheme.typography.bodySmall,
            color = FortuneColors.textSecondary,
        )
    }
}
