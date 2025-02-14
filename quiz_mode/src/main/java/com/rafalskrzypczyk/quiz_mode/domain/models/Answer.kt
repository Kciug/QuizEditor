package com.rafalskrzypczyk.quiz_mode.domain.models

import com.rafalskrzypczyk.core.utils.generateId
import com.rafalskrzypczyk.quiz_mode.data.AnswerDTO

data class Answer(
    val id: Int,
    val answerText: String,
    val isCorrect: Boolean
) {
    companion object {
        fun new(answerText: String): Answer {
            return Answer(
                id = Int.generateId(),
                answerText = answerText,
                isCorrect = false
            )
        }
    }
}

fun AnswerDTO.toDomain() = Answer(
    id = id,
    answerText = answerText,
    isCorrect = isCorrect
)

fun Answer.toDTO() = AnswerDTO(
    id = id,
    answerText = answerText,
    isCorrect = isCorrect
)

