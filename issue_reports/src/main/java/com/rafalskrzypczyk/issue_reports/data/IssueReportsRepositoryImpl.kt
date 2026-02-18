package com.rafalskrzypczyk.issue_reports.data

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.domain.models.GameMode
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.issue_reports.domain.IssueReportsRepository
import com.rafalskrzypczyk.issue_reports.domain.models.IssueReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IssueReportsRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi
) : IssueReportsRepository {

    override fun getIssueReports(): Flow<Response<List<IssueReport>>> {
        return firestoreApi.getIssueReports().map { response ->
            when (response) {
                is Response.Success -> {
                    Response.Success(response.data.map { dto ->
                        IssueReport(
                            id = dto.id,
                            questionId = dto.questionId,
                            questionContent = dto.questionContent,
                            description = dto.description,
                            gameMode = GameMode.fromString(dto.gameMode),
                            date = dto.timestamp.toDate()
                        )
                    })
                }
                is Response.Error -> Response.Error(response.error)
                is Response.Loading -> Response.Loading
            }
        }
    }

    override suspend fun deleteIssueReport(reportId: String): Response<Unit> {
        return firestoreApi.deleteIssueReport(reportId)
    }
}
