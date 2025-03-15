package com.rafalskrzypczyk.home.presentation

import com.rafalskrzypczyk.auth.domain.UserManager
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home.StatisticsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeScreenPresenter @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val userManager: UserManager,
    private val sharedPreferences: SharedPreferencesApi,
    private val resourceProvider: ResourceProvider,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : BasePresenter<HomeScreenContract.View>(), HomeScreenContract.Presenter {
    private var presenterScope: CoroutineScope? = null

    private var lastEditedMode: Int = 0

    override fun onViewCreated() {
        super.onViewCreated()
        presenterScope = CoroutineScope(SupervisorJob() + dispatcher)

        lastEditedMode = sharedPreferences.getLastEditedMode()
        if (lastEditedMode == 0) view.setStartWorkGuide()

        val userName = userManager.getCurrentLoggedUser()?.name
        if(userName != null) view.displayUserName(userName)
        else view.displayError(resourceProvider.getString(R.string.error_user_name_not_found))

        displayStatistics()
    }

    override fun onContinueWork() {
        if(lastEditedMode == 0) view.displayError("lastEditMode = 0")
        else view.navigateToDestination(lastEditedMode)
    }

    private fun displayStatistics() {
        presenterScope?.launch {
            statisticsRepository.getStatistics().collectLatest {
                when (it) {
                    is Response.Loading -> view.displayLoading()
                    is Response.Error -> view.displayError(it.error)
                    is Response.Success -> view.displayStatistics(it.data)
                }
            }
        }
    }
}