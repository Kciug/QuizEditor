package com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models

import com.rafalskrzypczyk.quiz_mode.domain.models.Answer

data class AnswerUIModel(
    val id: Long,
    var answerText: String,
    var isCorrect: Boolean
)

fun Answer.toSimplePresentation() : AnswerUIModel {
    return AnswerUIModel(
        id = id,
        answerText = answerText,
        isCorrect = isCorrect
    )
}
