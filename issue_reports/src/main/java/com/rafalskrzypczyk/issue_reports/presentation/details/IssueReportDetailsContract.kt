package com.rafalskrzypczyk.issue_reports.presentation.details

import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.issue_reports.presentation.list.ui_models.IssueReportUIModel

interface IssueReportDetailsContract {
    interface View : BaseContract.View {
        fun displayReportDetails(report: IssueReportUIModel)
        fun showCloseReportDialog()
        fun navigateToQuestionEditor(gameMode: String, questionId: Long)
        fun closeDetails()
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getReportDetails(reportId: String)
        fun onOpenQuestionClicked()
        fun onCloseReportConfirmed()
    }
}
