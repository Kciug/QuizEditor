package com.rafalskrzypczyk.quiz_mode.domain.mappers

import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel
import com.rafalskrzypczyk.quiz_mode.domain.models.Category

fun Category.toSimplePresentation() : SimpleCategoryUIModel {
    return SimpleCategoryUIModel(
        name = title,
        color = color.toLong()
    )
}
