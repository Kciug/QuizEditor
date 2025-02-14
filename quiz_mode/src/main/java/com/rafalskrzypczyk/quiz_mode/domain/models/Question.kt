package com.rafalskrzypczyk.quiz_mode.domain.models

import android.icu.util.Calendar
import com.rafalskrzypczyk.core.utils.generateId
import com.rafalskrzypczyk.quiz_mode.data.QuestionDTO
import java.util.Date

data class Question(
    val id: Int,
    var text: String,
    val answers: MutableList<Answer>,
    val creationDate: Date,
    val createdBy: String,
    val linkedCategories: MutableList<Int>
) {
    companion object {
        fun new(text: String): Question {
            return Question(
                id = Int.generateId(),
                text = text,
                answers = mutableListOf(),
                creationDate = Date(),
                createdBy = "Kurwa Chuj",
                linkedCategories = mutableListOf()
            )
        }
    }
}

fun QuestionDTO.toDomain() = Question(
    id = id.toInt(),
    text = "",
    answers = answersObject.map { it.toDomain() }.toMutableList(),
    creationDate = Date(),
    createdBy = "",
    linkedCategories = categoryIDs.toMutableList()
)

fun Question.toDTO() = QuestionDTO(
    id = id,
    questionText = text,
    answersObject = answers.map { it.toDTO() },
    categoryIDs = linkedCategories,
    dateCreated = creationDate,
    answersCount = answers.count(),
    correctAnswersCount = answers.filter { it.isCorrect }.count()
)