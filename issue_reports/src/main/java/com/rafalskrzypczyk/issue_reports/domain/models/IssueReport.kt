package com.rafalskrzypczyk.issue_reports.domain.models

import com.rafalskrzypczyk.core.domain.models.GameMode
import java.util.Date

data class IssueReport(
    val id: String,
    val questionId: Long,
    val questionContent: String,
    val description: String,
    val gameMode: GameMode,
    val date: Date
)
