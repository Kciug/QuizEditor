package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import androidx.annotation.StringRes
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.CategoryStatus

sealed class CategoryFilters(@StringRes val title: Int) {
    object None: CategoryFilters(R.string.filter_none)
    object WithQuestions: CategoryFilters(R.string.filter_by_having_questions)
    object WithoutQuestions: CategoryFilters(R.string.filter_by_no_questions)
    data class ByStatus(val status: CategoryStatus?): CategoryFilters(R.string.filter_by_status)
    object IsMigrated: CategoryFilters(R.string.filter_by_migrated)

    companion object{
        val defaultFilter = None

        fun getFilters() = listOf(None, WithQuestions, WithoutQuestions, ByStatus(null), IsMigrated)
    }
}