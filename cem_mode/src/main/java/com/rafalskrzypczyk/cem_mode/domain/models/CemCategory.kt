package com.rafalskrzypczyk.cem_mode.domain.models

import android.graphics.Color
import com.rafalskrzypczyk.core.base.Identifiable
import com.rafalskrzypczyk.core.domain.models.CategoryStatus
import com.rafalskrzypczyk.core.domain.models.toCategoryStatus
import com.rafalskrzypczyk.core.domain.models.toTitleString
import com.rafalskrzypczyk.core.extensions.formatToDataDate
import com.rafalskrzypczyk.core.extensions.generateId
import com.rafalskrzypczyk.firestore.data.models.CategoryColorRGB
import com.rafalskrzypczyk.firestore.data.models.CemCategoryDTO
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class CemCategory(
    override val id: Long,
    var title: String,
    var description: String,
    val linkedQuestions: MutableList<Long>,
    val linkedSubcategories: MutableList<Long>,
    val parentCategoryID: Long?,
    var status: CategoryStatus,
    var color: Int,
    var isFree: Boolean,
    val creationDate: Date,
    var modifiedDate: String,
    val productionTransferDate: Date?
) : Identifiable {
    val isUpToDate: Boolean
        get() {
            if (productionTransferDate == null) return false
            val parsedModifiedDate = try {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(modifiedDate)
            } catch (e: Exception) {
                null
            } ?: return false
            return productionTransferDate.after(parsedModifiedDate) || productionTransferDate == parsedModifiedDate
        }

    companion object {
        const val ROOT_ID = -1L

        fun new(title: String, color: Int, parentCategoryID: Long? = null) = CemCategory(
            id = Long.generateId(),
            title = title,
            description = "",
            linkedQuestions = mutableListOf(),
            linkedSubcategories = mutableListOf(),
            parentCategoryID = parentCategoryID,
            status = CategoryStatus.DRAFT,
            color = color,
            isFree = false,
            creationDate = Date(),
            modifiedDate = Date().formatToDataDate(),
            productionTransferDate = null
        )
    }
}

fun CemCategoryDTO.toDomain() = CemCategory(
    id = id,
    title = title,
    description = subtitle ?: "",
    linkedQuestions = questionIDs.toMutableList(),
    linkedSubcategories = subcategoryIDs.toMutableList(),
    parentCategoryID = parentCategoryID,
    status = status.toCategoryStatus(),
    color = color?.toAndroidColor() ?: Color.WHITE,
    isFree = free,
    creationDate = dateCreated,
    modifiedDate = dateModified,
    productionTransferDate = productionTransferDate
)

fun CemCategory.toDTO() = CemCategoryDTO(
    id = id,
    title = title,
    subtitle = description,
    questionIDs = linkedQuestions,
    subcategoryIDs = linkedSubcategories,
    parentCategoryID = parentCategoryID,
    status = status.toTitleString(),
    color = color.toCategoryColorRGB(),
    free = isFree,
    dateCreated = creationDate,
    questionsCount = linkedQuestions.count(),
    subcategoriesCount = linkedSubcategories.count(),
    dateModified = modifiedDate,
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
