package com.rafalskrzypczyk.quiz_mode.domain.models

import android.graphics.Color
import com.rafalskrzypczyk.core.base.Identifiable
import com.rafalskrzypczyk.core.extensions.generateId
import com.rafalskrzypczyk.firestore.data.models.CategoryColorRGB
import com.rafalskrzypczyk.firestore.data.models.CategoryDTO
import java.util.Date

data class Category(
    override val id: Long,
    var title: String,
    var description: String,
    val linkedQuestions: MutableList<Long>,
    var status: CategoryStatus,
    var color: Int,
    var isFree: Boolean,
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
            isFree = false,
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
    color = color?.toAndroidColor() ?: Color.WHITE,
    isFree = free,
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
    color = color.toCategoryColorRGB(),
    free = isFree,
    dateCreated = creationDate,
    questionCount = linkedQuestions.count(),
    dateModified = Date().toString(),
    productionTransferDate = productionTransferDate
)

private fun CategoryColorRGB.toAndroidColor(): Int {
    return Color.argb(
        opacity.times(255).toInt(),
        red.times(255).toInt(),
        green.times(255).toInt(),
        blue.times(255).toInt()
    )
}

private fun Int.toCategoryColorRGB(): CategoryColorRGB {
    return CategoryColorRGB(
        opacity = Color.alpha(this) / 255f,
        red = Color.red(this) / 255f,
        green = Color.green(this) / 255f,
        blue = Color.blue(this) / 255f
    )
}

