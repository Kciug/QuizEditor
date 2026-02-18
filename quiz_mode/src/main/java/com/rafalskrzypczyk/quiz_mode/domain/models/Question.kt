package com.rafalskrzypczyk.quiz_mode.domain.models

import com.rafalskrzypczyk.core.base.Identifiable
import com.rafalskrzypczyk.core.extensions.generateId
import com.rafalskrzypczyk.firestore.data.models.QuestionDTO
import java.util.Date

data class Question(
    override val id: Long,
    var text: String,
    var explanation: String,
    val answers: MutableList<Answer>,
    val creationDate: Date,
    val createdBy: String,
    val linkedCategories: MutableList<Long>,
    val dateModified: String
) : Identifiable {
    companion object {
        fun new(text: String): Question {
            return Question(
                id = Long.generateId(),
                text = text,
                explanation = "",
                answers = mutableListOf(),
                creationDate = Date(),
                createdBy = "Random User",
                linkedCategories = mutableListOf(),
                dateModified = Date().toString()
            )
        }
    }
}

fun QuestionDTO.toDomain() = Question(
    id = id,
    text = questionText,
    explanation = explanation,
    answers = answers.map { it.toDomain() }.toMutableList(),
    creationDate = dateCreated,
    createdBy = "",
    linkedCategories = categoryIDs.toMutableList(),
    dateModified = dateModified
)

fun Question.toDTO() = QuestionDTO(
    id = id,
    questionText = text,
    explanation = explanation,
    answers = answers.map { it.toDTO() },
    categoryIDs = linkedCategories,
    dateCreated = creationDate,
    dateModified = Date().toString()
)