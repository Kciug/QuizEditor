package com.rafalskrzypczyk.quiz_mode.domain.models

import com.rafalskrzypczyk.core.base.Identifiable
import com.rafalskrzypczyk.core.extensions.generateId
import com.rafalskrzypczyk.firestore.data.models.AnswerDTO
import java.util.Date

data class Answer(
    override val id: Long,
    var answerText: String,
    var isCorrect: Boolean,
    val dateCreated: Date,
    val dateModified: String
) : Identifiable {
    companion object {
        fun new(answerText: String): Answer {
            return Answer(
                id = Long.generateId(),
                answerText = answerText,
                isCorrect = false,
                dateCreated = Date(),
                dateModified = Date().toString()
            )
        }
    }
}

fun AnswerDTO.toDomain() = Answer(
    id = id,
    answerText = answerText,
    isCorrect = isCorrect,
    dateCreated = dateCreated,
    dateModified = dateModified
)

fun Answer.toDTO() = AnswerDTO(
    id = id,
    answerText = answerText,
    isCorrect = isCorrect,
    dateCreated = dateCreated,
    dateModified = Date().toString()
)

