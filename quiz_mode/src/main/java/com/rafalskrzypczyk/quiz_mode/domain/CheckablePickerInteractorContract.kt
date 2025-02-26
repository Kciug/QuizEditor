package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import kotlinx.coroutines.flow.Flow

interface CheckablePickerInteractorContract {
    fun getItemList() : Flow<List<Checkable>>
    fun onItemSelected(selectedItem: Checkable)
    fun onItemDeselected(deselectedItem: Checkable)
}