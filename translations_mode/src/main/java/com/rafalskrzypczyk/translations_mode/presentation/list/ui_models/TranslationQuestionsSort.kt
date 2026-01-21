package com.rafalskrzypczyk.translations_mode.presentation.list.ui_models

import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.translations_mode.R

sealed class TranslationQuestionsSort {
    enum class SortOptions(val resId: Int) {
        ByDate(R.string.sort_by_date),
        ByPhrase(R.string.sort_by_phrase),
        ByTranslationCount(R.string.sort_by_translation_count)
    }

    enum class SortTypes(val resId: Int) {
        Ascending(R.string.sort_type_ascending),
        Descending(R.string.sort_type_descending)
    }

    companion object {
        val defaultSortOption = SortOptions.ByDate
        val defaultSortType = SortTypes.Descending

        fun getSortOptions() = SortOptions.entries
        fun getSortTypes() = SortTypes.entries

        fun SortOptions.toSelectableMenuItem(isSelected: Boolean) = SelectableMenuItem(
            itemHashCode = this.ordinal,
            title = this.resId,
            isSelected = isSelected
        )

        fun SortTypes.toSelectableMenuItem(isSelected: Boolean) = SelectableMenuItem(
            itemHashCode = this.ordinal,
            title = this.resId,
            isSelected = isSelected
        )

        fun SelectableMenuItem.toSortOption() = SortOptions.entries.find { it.ordinal == this.itemHashCode }
        fun SelectableMenuItem.toSortType() = SortTypes.entries.find { it.ordinal == this.itemHashCode }
    }
}