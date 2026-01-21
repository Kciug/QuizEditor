package com.rafalskrzypczyk.translations_mode.presentation.list.ui_models

import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.translations_mode.R

sealed class TranslationQuestionsFilters(val resId: Int) {
    data object None : TranslationQuestionsFilters(R.string.filter_none)
    data object WithTranslations : TranslationQuestionsFilters(R.string.filter_with_translations)
    data object WithoutTranslations : TranslationQuestionsFilters(R.string.filter_without_translations)

    companion object {
        val defaultFilter = None

        fun getFilters() = listOf(None, WithTranslations, WithoutTranslations)

        fun TranslationQuestionsFilters.toSelectableMenuItem(isSelected: Boolean) = SelectableMenuItem(
            itemHashCode = when(this) {
                is None -> 0
                is WithTranslations -> 1
                is WithoutTranslations -> 2
            },
            title = this.resId,
            isSelected = isSelected
        )

        fun SelectableMenuItem.toFilterOption() = when(this.itemHashCode) {
            0 -> None
            1 -> WithTranslations
            2 -> WithoutTranslations
            else -> None
        }
    }
}