package com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker

import android.util.Log
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.quiz_mode.domain.CheckablePickerInteractorContract
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CheckablePickerPresenter (
    private val view: CheckablePickerContract.View,
    private val interactor: CheckablePickerInteractorContract
) : BasePresenter(), CheckablePickerContract.Presenter {
    private val searchQuery = MutableStateFlow("")

    override fun getItemList() {
        presenterScope.launch {
            combine(
                interactor.getItemList(),
                searchQuery
            ){ items, query ->
                items.filter { it.title.contains(query, ignoreCase = true) }
            }.collectLatest {
                view.displayData(it)
            }
        }
    }

    override fun onItemSelected(selectedItem: Checkable) {
        Log.d("KURWA", "CheckablePickerPresenter: onItemSelected: $selectedItem")
        interactor.onItemSelected(selectedItem)
    }

    override fun onItemDeselected(deselectedItem: Checkable) {
        interactor.onItemDeselected(deselectedItem)
    }

    override fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}