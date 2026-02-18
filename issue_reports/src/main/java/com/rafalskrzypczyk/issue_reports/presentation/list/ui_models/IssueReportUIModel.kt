package com.rafalskrzypczyk.issue_reports.presentation.list.ui_models

import com.rafalskrzypczyk.core.domain.models.GameMode

data class IssueReportUIModel(
    val id: String,
    val questionId: Long,
    val questionContent: String,
    val description: String,
    val gameMode: GameMode,
    val dateString: String
)
