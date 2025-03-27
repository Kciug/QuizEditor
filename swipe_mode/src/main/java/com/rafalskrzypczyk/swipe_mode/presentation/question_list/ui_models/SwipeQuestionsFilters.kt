package com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models

import androidx.annotation.StringRes
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.swipe_mode.R

sealed class SwipeQuestionsFilters(@StringRes val title: Int) {
    object None : SwipeQuestionsFilters(R.string.filter_none)
    object AreCorrect : SwipeQuestionsFilters(R.string.filter_by_correct)
    object AreIncorrect : SwipeQuestionsFilters(R.string.filter_by_incorrect)

    companion object{
        val defaultFilter = None

        fun getFilters() = listOf(None, AreCorrect, AreIncorrect)

        fun SwipeQuestionsFilters.toSelectableMenuItem(isSelected: Boolean, subMenu: List<SelectableMenuItem>? = null) : SelectableMenuItem = SelectableMenuItem(
            itemHashCode = hashCode(),
            title = title,
            isSelected = isSelected,
            subMenu = subMenu
        )

        fun SelectableMenuItem.toFilterOption() : SwipeQuestionsFilters? = getFilters().find { it.hashCode() == itemHashCode }
    }
}