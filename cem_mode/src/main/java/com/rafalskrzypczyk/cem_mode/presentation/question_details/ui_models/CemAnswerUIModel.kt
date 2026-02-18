package com.rafalskrzypczyk.cem_mode.presentation.question_details.ui_models

import com.rafalskrzypczyk.cem_mode.domain.models.CemAnswer

data class CemAnswerUIModel(
    val id: Long,
    var answerText: String,
    var isCorrect: Boolean
)

fun CemAnswer.toUIModel() = CemAnswerUIModel(
    id = id,
    answerText = text,
    isCorrect = isCorrect
)
