package com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker

import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable

interface CheckablePickerContract {
    interface View{
        fun displayData(items: List<Checkable>)
    }
    interface Presenter{
        fun getItemList()
        fun onItemSelected(selectedItem: Checkable)
        fun onItemDeselected(deselectedItem: Checkable)
        fun onSearchQueryChanged(query: String)
    }
}