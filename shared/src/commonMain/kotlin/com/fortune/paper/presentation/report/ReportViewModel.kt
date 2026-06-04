package com.fortune.paper.presentation.report

import com.fortune.paper.core.toad.ToadViewModel

class ReportViewModel(deps: ReportDependencies) :
    ToadViewModel<ReportState, ReportEvent>(
        initialState = ReportState(),
        dependencies = deps,
    )
