package com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.di.MainDispatcher
import com.rafalskrzypczyk.quiz_mode.domain.CheckablePickerInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckablePickerPresenter @Inject constructor (
    @MainDispatcher dispatcher: CoroutineDispatcher,
) : BasePresenter<CheckablePickerContract.View>(), CheckablePickerContract.Presenter {
    private lateinit var interactor: CheckablePickerInteractor

    private val presenterScope = CoroutineScope(SupervisorJob() + dispatcher)

    private val searchQuery = MutableStateFlow("")

    override fun onViewCreated() {
        super.onViewCreated()

        presenterScope.launch {
            combine(
                interactor.getItemList(),
                searchQuery
            ){ response, query ->
                when (response) {
                    is Response.Success -> Response.Success(response.data.filter { it.title.contains(query, ignoreCase = true) })
                    is Response.Error -> response
                    is Response.Loading -> response
                }
            }.collectLatest {
                when (it) {
                    is Response.Success -> view.displayData(it.data)
                    is Response.Error -> view.showError(it.error)
                    is Response.Loading -> view.showLoading()
                }
            }
        }
    }

    override fun attachInteractor(interactor: CheckablePickerInteractor) {
        this.interactor = interactor
    }

    override fun onItemSelected(selectedItem: Checkable) {
        interactor.onItemSelected(selectedItem)
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        interactor.onItemDeselected(deselectedItem)
    }

    override fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    override fun onDestroy() {
        presenterScope.cancel()
        super.onDestroy()
    }
}