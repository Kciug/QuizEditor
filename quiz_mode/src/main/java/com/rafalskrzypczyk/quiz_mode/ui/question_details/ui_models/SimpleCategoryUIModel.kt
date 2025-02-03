package com.rafalskrzypczyk.quiz_mode.ui.question_details.ui_models

import com.rafalskrzypczyk.quiz_mode.models.Category

data class SimpleCategoryUIModel(
    val name: String,
    val color: Long
)

fun Category.toPresentation() : SimpleCategoryUIModel {
    return SimpleCategoryUIModel(
        name = title,
        color = color
    )
}
