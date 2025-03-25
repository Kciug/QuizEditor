package com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models

import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion

data class SwipeQuestionSimpleUIModel(
    val id: Long,
    val text: String,
    val isCorrect: Boolean
)

fun SwipeQuestion.toSimpleUIModel() = SwipeQuestionSimpleUIModel(
    id = id,
    text = text,
    isCorrect = isCorrect
)
