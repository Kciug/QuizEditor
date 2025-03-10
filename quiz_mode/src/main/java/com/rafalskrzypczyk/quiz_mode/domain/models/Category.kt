package com.rafalskrzypczyk.quiz_mode.domain.models

import android.graphics.Color
import com.rafalskrzypczyk.core.base.Identifiable
import com.rafalskrzypczyk.core.extensions.generateId
import com.rafalskrzypczyk.firestore.data.models.CategoryColorRGB
import com.rafalskrzypczyk.firestore.data.models.CategoryDTO
import com.rafalskrzypczyk.quiz_mode.domain.CategoryStatus
import com.rafalskrzypczyk.quiz_mode.domain.toCategoryStatus
import com.rafalskrzypczyk.quiz_mode.domain.toTitleString
import java.util.Date

data class Category(
    override val id: Long,
    var title: String,
    var description: String,
    val linkedQuestions: MutableList<Long>,
    var status: CategoryStatus,
    var color: Int,
    val creationDate: Date,
    val createdBy: String,
    val modifiedDate: String,
    val productionTransferDate: Date?
) : Identifiable {
    companion object {
        fun new(title: String, color: Int) = Category(
            id = Long.generateId(),
            title = title,
            description = "",
            linkedQuestions = mutableListOf(),
            status = CategoryStatus.DRAFT,
            color = color,
            creationDate = Date(),
            createdBy = "Random User",
            modifiedDate = Date().toString(),
            productionTransferDate = null
        )
    }
}

fun CategoryDTO.toDomain() = Category(
    id = id,
    title = title,
    description = subtitle ?: "",
    linkedQuestions = questionIDs.toMutableList(),
    status = status.toCategoryStatus(),
    color = Color.argb(
        (color?.opacity?.times(255)?.toInt() ?: 0),
        (color?.red?.times(255)?.toInt() ?: 0),
        (color?.green?.times(255)?.toInt() ?: 0),
        (color?.blue?.times(255)?.toInt() ?: 0)),
    creationDate = dateCreated,
    createdBy = "Random User",
    modifiedDate = dateModified,
    productionTransferDate = productionTransferDate
)

fun Category.toDTO() = CategoryDTO(
    id = id,
    title = title,
    subtitle = description,
    questionIDs = linkedQuestions,
    status = status.toTitleString(),
    color = CategoryColorRGB(
        opacity = Color.alpha(color) / 255f,
        red = Color.red(color) / 255f,
        green = Color.green(color) / 255f,
        blue = Color.blue(color) / 255f
    ),
    dateCreated = creationDate,
    questionCount = linkedQuestions.count(),
    dateModified = Date().toString(),
    productionTransferDate = productionTransferDate
)

