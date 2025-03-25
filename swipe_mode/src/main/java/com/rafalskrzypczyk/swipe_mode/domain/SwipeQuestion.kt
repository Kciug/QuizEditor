package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.core.base.Identifiable
import com.rafalskrzypczyk.core.extensions.generateId
import com.rafalskrzypczyk.firestore.data.models.SwipeQuestionDTO
import java.util.Date

data class SwipeQuestion(
    override val id: Long,
    var text: String,
    var isCorrect: Boolean,
    val dateCreated: Date,
    var dateModified: Date,
) : Identifiable {
    companion object {
        fun new(text: String, isCorrect: Boolean) = SwipeQuestion(
            id = Long.generateId(),
            text = text,
            isCorrect = isCorrect,
            dateCreated = Date(),
            dateModified = Date()
        )
    }
}

fun SwipeQuestionDTO.toDomain() = SwipeQuestion(
    id = id,
    text = text,
    isCorrect = isCorrect,
    dateCreated = dateCreated,
    dateModified = dateModified
)

fun SwipeQuestion.toDTO() = SwipeQuestionDTO(
    id = id,
    text = text,
    isCorrect = isCorrect,
    dateCreated = dateCreated,
    dateModified = dateModified
)
