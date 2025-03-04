package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import androidx.annotation.StringRes
import com.rafalskrzypczyk.quiz_mode.R

sealed class QuestionSort {
    sealed class SortOptions(@StringRes val title: Int) {
        object ByDate : SortOptions(R.string.sort_by_date)
        object ByTitle : SortOptions(R.string.sort_by_title)
        object ByAnswersAmount : SortOptions(R.string.sort_by_answers_amount)
    }
    sealed class SortTypes(@StringRes val title: Int) {
        object Ascending : SortTypes(R.string.sort_ascending)
        object Descending : SortTypes(R.string.sort_descending)
    }

    companion object {
        val defaultSortOption = SortOptions.ByDate
        val defaultSortType = SortTypes.Ascending

        fun getSortOptions() = listOf(SortOptions.ByDate, SortOptions.ByTitle, SortOptions.ByAnswersAmount)
        fun getSortTypes() = listOf(SortTypes.Ascending, SortTypes.Descending)
    }
}