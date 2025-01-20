package com.rafalskrzypczyk.quiz_mode

import android.content.Context
import android.graphics.Color

enum class CategoryStatus {
    DRAFT,
    IN_PROGRESS,
    DONE,
    APPROVED
}

fun CategoryStatus.getTitle(context: Context) : String {
    return when(this) {
        CategoryStatus.DRAFT -> context.getString(R.string.status_draft)
        CategoryStatus.IN_PROGRESS -> context.getString(R.string.status_in_progress)
        CategoryStatus.DONE -> context.getString(R.string.status_done)
        CategoryStatus.APPROVED -> context.getString(R.string.status_approved)
    }
}

fun CategoryStatus.getColor(context: Context) : Int {
    return when(this) {
        CategoryStatus.DRAFT -> context.getColor(R.color.status_draft)
        CategoryStatus.IN_PROGRESS -> context.getColor(R.color.status_in_progress)
        CategoryStatus.DONE -> context.getColor(R.color.status_done)
        CategoryStatus.APPROVED -> context.getColor(R.color.status_approved)
    }
}