package com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models

import androidx.annotation.StringRes
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.R

sealed class QuestionFilter(@StringRes val title: Int) {
    object None : QuestionFilter(R.string.filter_none)
    object WithCategories : QuestionFilter(R.string.filter_by_having_categories)
    object WithoutCategories : QuestionFilter(R.string.filter_by_no_categories)
    object WithAnswers : QuestionFilter(R.string.filter_by_having_answers)
    object WithoutAnswers : QuestionFilter(R.string.filter_by_no_answers)
    object WithCorrectAnswers : QuestionFilter(R.string.filter_by_correct_answers)
    object WithoutCorrectAnswers : QuestionFilter(R.string.filter_by_no_correct_answers)

    companion object{
        val defaultFilter = None

        fun getFilters() = listOf(None, WithCategories, WithoutCategories, WithAnswers, WithoutAnswers, WithCorrectAnswers, WithoutCorrectAnswers)

        fun QuestionFilter.toSelectableMenuItem(isSelected: Boolean, subMenu: List<SelectableMenuItem>? = null) : SelectableMenuItem = SelectableMenuItem(
            itemHashCode = hashCode(),
            title = title,
            isSelected = isSelected,
            subMenu = subMenu
        )

        fun SelectableMenuItem.toFilterOption() : QuestionFilter? = getFilters().find { it.hashCode() == itemHashCode }
    }
}