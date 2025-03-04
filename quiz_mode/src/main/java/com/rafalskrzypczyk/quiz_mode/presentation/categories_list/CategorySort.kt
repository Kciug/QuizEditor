package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import androidx.annotation.StringRes
import com.rafalskrzypczyk.quiz_mode.R

sealed class CategorySort {
    sealed class SortOptions(@StringRes val title: Int) {
        object ByDate: SortOptions(R.string.sort_by_date)
        object ByTitle: SortOptions(R.string.sort_by_title)
        object ByQuestionsAmount: SortOptions(R.string.sort_by_questions_amount)
    }
    sealed class SortTypes(@StringRes val title: Int) {
        object Ascending: SortTypes(R.string.sort_ascending)
        object Descending: SortTypes(R.string.sort_descending)
    }

    companion object{
        val defaultSortOption = SortOptions.ByDate
        val defaultSortType = SortTypes.Ascending

        fun getSortOptions() = listOf(SortOptions.ByDate, SortOptions.ByTitle, SortOptions.ByQuestionsAmount)
        fun getSortTypes() = listOf(SortTypes.Ascending, SortTypes.Descending)
    }
}