package com.rafalskrzypczyk.issue_reports.presentation.details

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.issue_reports.domain.IssueReportsRepository
import com.rafalskrzypczyk.issue_reports.domain.models.IssueReport
import com.rafalskrzypczyk.issue_reports.presentation.list.ui_models.IssueReportUIModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class IssueReportDetailsPresenter @Inject constructor(
    private val repository: IssueReportsRepository
) : BasePresenter<IssueReportDetailsContract.View>(), IssueReportDetailsContract.Presenter {

    private var currentReport: IssueReport? = null

    override fun getReportDetails(reportId: String) {
        presenterScope?.launch {
            repository.getIssueReports().collectLatest { response ->
                if (response is Response.Success) {
                    val report = response.data.find { it.id == reportId }
                    if (report != null) {
                        currentReport = report
                        view?.displayReportDetails(
                            IssueReportUIModel(
                                id = report.id,
                                questionId = report.questionId,
                                questionContent = report.questionContent,
                                description = report.description,
                                gameMode = report.gameMode,
                                dateString = String.formatDate(report.date)
                            )
                        )
                    } else {
                        view?.displayError("Report not found")
                    }
                }
            }
        }
    }

    override fun onOpenQuestionClicked() {
        currentReport?.let {
            view?.navigateToQuestionEditor(it.gameMode.value, it.questionId)
        }
    }

    override fun onCloseReportConfirmed() {
        presenterScope?.launch {
            currentReport?.let { report ->
                repository.deleteIssueReport(report.id).let { response ->
                    if (response is Response.Success) {
                        view?.closeDetails()
                    } else if (response is Response.Error) {
                        view?.displayError(response.error)
                    }
                }
            }
        }
    }
}
