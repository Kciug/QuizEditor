package com.rafalskrzypczyk.quiz_mode.domain.models

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.rafalskrzypczyk.quiz_mode.R

enum class CategoryStatus(@StringRes val title: Int, @ColorRes val color: Int) {
    DRAFT(R.string.status_draft, R.color.status_draft),
    IN_PROGRESS(R.string.status_in_progress, R.color.status_in_progress),
    DONE(R.string.status_done, R.color.status_done),
    APPROVED(R.string.status_approved, R.color.status_approved),
    NEED_REWORK(R.string.status_need_rework, R.color.status_need_rework)
}

fun CategoryStatus.toTitleString() : String {
    return when(this) {
        CategoryStatus.DRAFT -> "Draft"
        CategoryStatus.IN_PROGRESS -> "In Progress"
        CategoryStatus.DONE -> "Done"
        CategoryStatus.APPROVED -> "Approved"
        CategoryStatus.NEED_REWORK -> "Needs Rework"
    }
}

fun String.toCategoryStatus() : CategoryStatus {
    return when(this) {
        "Draft" -> CategoryStatus.DRAFT
        "In Progress" -> CategoryStatus.IN_PROGRESS
        "Done" -> CategoryStatus.DONE
        "Approved" -> CategoryStatus.APPROVED
        "Needs Rework" -> CategoryStatus.NEED_REWORK
        else -> CategoryStatus.DRAFT
    }
}