package com.fortune.paper.presentation.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fortune.paper.presentation.report.actions.DismissOutOfPaper
import com.fortune.paper.presentation.report.actions.LoadReport
import com.fortune.paper.presentation.report.actions.RefreshReport
import com.fortune.paper.presentation.report.components.ErrorReport
import com.fortune.paper.presentation.report.components.LoadingReport
import com.fortune.paper.presentation.report.components.OutOfPaperDialog
import com.fortune.paper.presentation.report.components.ReportHomeShell
import com.fortune.paper.presentation.report.components.ReportRevealed
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportScreen(
    onNavigateToSettings: () -> Unit = {},
    viewModel: ReportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) { viewModel.dispatch(LoadReport) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ReportEvent.ShowError -> { /* 인라인 state.error 로 표시 */ }
                ReportEvent.NavigateToSettings -> onNavigateToSettings()
            }
        }
    }

    ReportHomeShell(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.dispatch(RefreshReport) },
        onTabSettings = onNavigateToSettings,
    ) {
        when {
            state.isLoading -> LoadingReport()
            state.report != null -> ReportRevealed(state.report!!)
            state.error != null -> ErrorReport(state.error!!) { viewModel.dispatch(LoadReport) }
            else -> LoadingReport()
        }
    }

    if (state.outOfPaper) {
        OutOfPaperDialog(onDismiss = { viewModel.dispatch(DismissOutOfPaper) })
    }
}
