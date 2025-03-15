package com.rafalskrzypczyk.home.presentation

import com.rafalskrzypczyk.core.base.BaseContract

interface HomeScreenContract {
    interface View : BaseContract.View {
        fun displayUserName(name: String)
        fun setStartWorkGuide()
        fun navigateToDestination(destination: Int)
        fun displayStatistics(statistics: DataStatisticsUIModel)
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun onContinueWork()
    }
}