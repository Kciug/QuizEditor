package com.rafalskrzypczyk.cem_mode.domain.models

import com.rafalskrzypczyk.core.base.Identifiable
import com.rafalskrzypczyk.core.extensions.generateId
import com.rafalskrzypczyk.firestore.data.models.CemAnswerDTO
import com.rafalskrzypczyk.firestore.data.models.CemQuestionDTO
import java.util.Date

data class CemQuestion(
    override val id: Long,
    var text: String,
    val answers: MutableList<CemAnswer>,
    val creationDate: Date,
    val linkedCategories: MutableList<Long>,
    val dateModified: String
) : Identifiable {
    companion object {
        fun new(text: String): CemQuestion {
            return CemQuestion(
                id = Long.generateId(),
                text = text,
                answers = mutableListOf(),
                creationDate = Date(),
                linkedCategories = mutableListOf(),
                dateModified = Date().toString()
            )
        }
    }
}

data class CemAnswer(
    override val id: Long,
    var text: String,
    var isCorrect: Boolean
) : Identifiable {
    companion object {
        fun new(text: String, isCorrect: Boolean) = CemAnswer(
            id = Long.generateId(),
            text = text,
            isCorrect = isCorrect
        )
    }
}

fun CemQuestionDTO.toDomain() = CemQuestion(
    id = id,
    text = questionText,
    answers = answers.map { it.toDomain() }.toMutableList(),
    creationDate = dateCreated,
    linkedCategories = categoryIDs.toMutableList(),
    dateModified = dateModified
)

fun CemQuestion.toDTO() = CemQuestionDTO(
    id = id,
    questionText = text,
    answers = answers.map { it.toDTO() },
    categoryIDs = linkedCategories,
    dateCreated = creationDate,
    dateModified = Date().toString()
)

fun CemAnswerDTO.toDomain() = CemAnswer(
    id = id,
    text = answerText,
    isCorrect = isCorrect
)

fun CemAnswer.toDTO() = CemAnswerDTO(
    id = id,
    answerText = text,
    isCorrect = isCorrect
)
