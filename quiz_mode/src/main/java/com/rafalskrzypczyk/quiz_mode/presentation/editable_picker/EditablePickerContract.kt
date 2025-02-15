package com.rafalskrzypczyk.quiz_mode.presentation.editable_picker

import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable

interface EditablePickerContract {
    interface View{
        fun displayData(items: List<Checkable>)
    }
    interface Presenter{
        fun setPickerView(view: View)
        fun getItemList()
        fun onItemSelected(selectedItem: Checkable)
        fun onItemDeselected(deselectedItem: Checkable)
        fun onSearchQueryChanged(query: String)
    }
}