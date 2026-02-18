package com.rafalskrzypczyk.issue_reports.presentation.list

import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.issue_reports.presentation.list.ui_models.IssueReportUIModel

interface IssueReportsContract {
    interface View : BaseContract.View {
        fun displayIssueReports(reports: List<IssueReportUIModel>)
        fun displayNoElementsView()
        fun displayElementsCount(count: Int)
        fun navigateToDetails(reportId: String)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getIssueReports()
        fun onReportClicked(report: IssueReportUIModel)
    }
}
