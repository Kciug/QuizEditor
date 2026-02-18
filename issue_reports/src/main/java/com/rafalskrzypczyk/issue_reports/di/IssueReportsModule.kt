package com.rafalskrzypczyk.issue_reports.di

import com.rafalskrzypczyk.issue_reports.data.IssueReportsRepositoryImpl
import com.rafalskrzypczyk.issue_reports.domain.IssueReportsRepository
import com.rafalskrzypczyk.issue_reports.presentation.details.IssueReportDetailsContract
import com.rafalskrzypczyk.issue_reports.presentation.details.IssueReportDetailsPresenter
import com.rafalskrzypczyk.issue_reports.presentation.list.IssueReportsContract
import com.rafalskrzypczyk.issue_reports.presentation.list.IssueReportsPresenter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IssueReportsModule {

    @Binds
    @Singleton
    abstract fun bindIssueReportsRepository(
        impl: IssueReportsRepositoryImpl
    ): IssueReportsRepository
}

@Module
@InstallIn(FragmentComponent::class)
abstract class IssueReportsFragmentModule {

    @Binds
    abstract fun bindIssueReportsPresenter(
        presenter: IssueReportsPresenter
    ): IssueReportsContract.Presenter

    @Binds
    abstract fun bindIssueReportDetailsPresenter(
        presenter: IssueReportDetailsPresenter
    ): IssueReportDetailsContract.Presenter
}
