package com.rafalskrzypczyk.quiz_mode.domain.models

import com.rafalskrzypczyk.core.utils.generateId

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
