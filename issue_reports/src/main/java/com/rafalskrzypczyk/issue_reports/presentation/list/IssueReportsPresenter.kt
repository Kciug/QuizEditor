package com.rafalskrzypczyk.issue_reports.presentation.list

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.issue_reports.domain.IssueReportsRepository
import com.rafalskrzypczyk.issue_reports.presentation.list.ui_models.IssueReportUIModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class IssueReportsPresenter @Inject constructor(
    private val repository: IssueReportsRepository
) : BasePresenter<IssueReportsContract.View>(), IssueReportsContract.Presenter {

    override fun getIssueReports() {
        presenterScope?.launch {
            repository.getIssueReports().collectLatest { response ->
                when (response) {
                    is Response.Loading -> view?.displayLoading()
                    is Response.Success -> {
                        val uiModels = response.data.map {
                            IssueReportUIModel(
                                id = it.id,
                                questionId = it.questionId,
                                questionContent = it.questionContent,
                                description = it.description,
                                gameMode = it.gameMode,
                                dateString = String.formatDate(it.date)
                            )
                        }
                        if (uiModels.isEmpty()) {
                            view?.displayNoElementsView()
                        } else {
                            view?.displayIssueReports(uiModels)
                            view?.displayElementsCount(uiModels.size)
                        }
                    }
                    is Response.Error -> view?.displayError(response.error)
                }
            }
        }
    }

    override fun onReportClicked(report: IssueReportUIModel) {
        view?.navigateToDetails(report.id)
    }
}
