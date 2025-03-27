package com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models

import androidx.annotation.StringRes
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.swipe_mode.R

sealed class SwipeQuestionsSort {
    sealed class SortOptions(@StringRes val title: Int) {
        object ByDate : SortOptions(R.string.sort_by_date)
        object ByTitle : SortOptions(R.string.sort_by_title)
    }
    sealed class SortTypes(@StringRes val title: Int) {
        object Ascending : SortTypes(R.string.sort_ascending)
        object Descending : SortTypes(R.string.sort_descending)
    }

    companion object {
        val defaultSortOption = SortOptions.ByDate
        val defaultSortType = SortTypes.Ascending

        fun getSortOptions() = listOf(SortOptions.ByDate, SortOptions.ByTitle)
        fun getSortTypes() = listOf(SortTypes.Ascending, SortTypes.Descending)

        fun SortOptions.toSelectableMenuItem(isSelected: Boolean) : SelectableMenuItem = SelectableMenuItem(
            itemHashCode = this.hashCode(),
            title = title,
            isSelected = isSelected
        )

        fun SortTypes.toSelectableMenuItem(isSelected: Boolean) : SelectableMenuItem = SelectableMenuItem(
            itemHashCode = hashCode(),
            title = title,
            isSelected = isSelected
        )

        fun SelectableMenuItem.toSortOption() : SortOptions? = getSortOptions().find { it.hashCode() == this.itemHashCode }

        fun SelectableMenuItem.toSortType() : SortTypes? = getSortTypes().find { it.hashCode() == this.itemHashCode }
    }
}