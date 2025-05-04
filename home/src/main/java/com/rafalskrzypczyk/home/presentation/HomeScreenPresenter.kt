package com.rafalskrzypczyk.home.presentation

import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home.StatisticsRepository
import javax.inject.Inject

class HomeScreenPresenter @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val userManager: UserManager,
    private val sharedPreferences: SharedPreferencesApi,
    private val resourceProvider: ResourceProvider
) : BasePresenter<HomeScreenContract.View>(), HomeScreenContract.Presenter {

    private var lastEditedMode: Int = 0

    override fun onViewCreated() {
        super.onViewCreated()

        lastEditedMode = sharedPreferences.getLastEditedMode()
        if (lastEditedMode == 0) view.setStartWorkGuide()

        val userName = userManager.getCurrentLoggedUser()?.name
        if(userName != null) view.displayUserName(userName)
        else view.displayError(resourceProvider.getString(R.string.error_user_name_not_found))
    }

    override fun onContinueWork() {
        if(lastEditedMode == 0) view.displayError("lastEditMode = 0")
        else view.navigateToDestination(lastEditedMode)
    }
}