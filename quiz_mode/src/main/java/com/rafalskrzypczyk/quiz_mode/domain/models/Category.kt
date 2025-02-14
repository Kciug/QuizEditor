package com.rafalskrzypczyk.quiz_mode.domain.models

import com.rafalskrzypczyk.core.utils.generateId
import com.rafalskrzypczyk.quiz_mode.data.CategoryDTO
import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus
import java.util.Date

data class Category(
    val id: Int,
    var title: String,
    var description: String,
    val linkedQuestions: MutableList<Int>,
    var status: CategoryStatus,
    var color: Int,
    val creationDate: Date,
    val createdBy: String
) {
    companion object {
        fun new(title: String) = Category(
            id = Int.generateId(),
            title = title,
            description = "",
            linkedQuestions = mutableListOf(),
            status = CategoryStatus.DRAFT,
            color = 0,
            creationDate = Date(),
            createdBy = "Random User"
        )
    }
}

fun CategoryDTO.toDomain() = Category(
    id = id,
    title = title,
    description = subtitle,
    linkedQuestions = questionIDs.toMutableList(),
    status = status,
    color = color,
    creationDate = dateCreated,
    createdBy = "Random User"
)

fun Category.toDTO() = CategoryDTO(
    id = id,
    title = title,
    subtitle = description,
    questionIDs = linkedQuestions,
    status = status,
    color = color,
    dateCreated = creationDate,
    questionAmount = linkedQuestions.count()
)
