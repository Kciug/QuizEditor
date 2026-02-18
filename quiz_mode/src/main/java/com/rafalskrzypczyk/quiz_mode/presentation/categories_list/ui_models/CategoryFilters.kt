package com.rafalskrzypczyk.quiz_mode.presentation.categories_list.ui_models

import androidx.annotation.StringRes
import com.rafalskrzypczyk.core.domain.models.CategoryStatus
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.quiz_mode.R
import kotlin.hashCode

sealed class CategoryFilters(@StringRes val title: Int) {
    object None: CategoryFilters(R.string.filter_none)
    object WithQuestions: CategoryFilters(R.string.filter_by_having_questions)
    object WithoutQuestions: CategoryFilters(R.string.filter_by_no_questions)
    data class ByStatus(val status: CategoryStatus?): CategoryFilters(R.string.filter_by_status)
    object IsMigrated: CategoryFilters(R.string.filter_by_migrated)

    companion object{
        val defaultFilter = None

        fun getFilters() = listOf(None, WithQuestions, WithoutQuestions, ByStatus(null), IsMigrated)

        fun CategoryFilters.toSelectableMenuItem(isSelected: Boolean, subMenu: List<SelectableMenuItem>? = null) : SelectableMenuItem = SelectableMenuItem(
            itemHashCode = hashCode(),
            title = title,
            isSelected = isSelected,
            subMenu = subMenu
        )

        fun CategoryStatus?.toSelectableMenuItem(isSelected: Boolean) : SelectableMenuItem = SelectableMenuItem(
            itemHashCode = hashCode(),
            title = this?.title!!,
            isSelected = isSelected
        )

        fun SelectableMenuItem.toFilterOption() : CategoryFilters? {
            val statusFilter = CategoryStatus.entries.find { it.hashCode() == itemHashCode }
            return if(statusFilter != null) ByStatus(statusFilter)
            else getFilters().find { it.hashCode() == itemHashCode }
        }
    }
}