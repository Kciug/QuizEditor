package com.rafalskrzypczyk.quiz_mode.ui.view_models

import com.rafalskrzypczyk.quiz_mode.models.Answer

data class AnswerUIModel(
    val id: Int,
    val answerText: String,
    val isCorrect: Boolean
)

fun Answer.toPresentation() : AnswerUIModel {
    return AnswerUIModel(
        id = id,
        answerText = answerText,
        isCorrect = isCorrect
    )
}

fun AnswerUIModel.toDomain() : Answer {
    return Answer(
        id = id,
        answerText = answerText,
        isCorrect = isCorrect
    )
}
