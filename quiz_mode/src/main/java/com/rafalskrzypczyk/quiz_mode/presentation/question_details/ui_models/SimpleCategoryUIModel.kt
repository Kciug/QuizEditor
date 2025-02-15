package com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models

import com.rafalskrzypczyk.quiz_mode.domain.models.Category

data class SimpleCategoryUIModel(
    val name: String,
    val color: Long
)

fun Category.toSimplePresentation() : SimpleCategoryUIModel {
    return SimpleCategoryUIModel(
        name = title,
        color = color.toLong()
    )
}
