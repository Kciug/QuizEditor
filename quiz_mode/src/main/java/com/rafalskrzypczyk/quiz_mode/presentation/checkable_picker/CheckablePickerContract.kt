package com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker

import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.quiz_mode.domain.CheckablePickerInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable

interface CheckablePickerContract {
    interface View : BaseContract.View {
        fun displayData(items: List<Checkable>)
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun attachInteractor(interactor: CheckablePickerInteractor)
        fun onItemSelected(selectedItem: Checkable)
        fun onItemDeselected(deselectedItem: Checkable)
        fun onSearchQueryChanged(query: String)
    }
}