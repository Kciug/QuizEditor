package com.rafalskrzypczyk.quiz_mode.domain

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import kotlinx.coroutines.flow.Flow

interface CheckablePickerInteractor {
    fun getItemList() : Flow<Response<List<Checkable>>>
    fun onItemSelected(selectedItem: Checkable)
    fun onItemDeselected(deselectedItem: Checkable)
}