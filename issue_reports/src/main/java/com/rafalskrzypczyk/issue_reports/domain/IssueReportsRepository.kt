package com.rafalskrzypczyk.issue_reports.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.issue_reports.domain.models.IssueReport
import kotlinx.coroutines.flow.Flow

interface IssueReportsRepository {
    fun getIssueReports(): Flow<Response<List<IssueReport>>>
    suspend fun deleteIssueReport(reportId: String): Response<Unit>
}
