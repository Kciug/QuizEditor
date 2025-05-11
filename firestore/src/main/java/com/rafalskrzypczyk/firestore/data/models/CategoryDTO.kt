package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class CategoryDTO(
    val id: Long = -1,
    val title: String = "",
    val subtitle: String? = null,
    val questionIDs: List<Long> = emptyList(),
    val status: String = "",
    val color: CategoryColorRGB? = null,
    val free: Boolean = false,
    val dateCreated: Date = Date(),
    val dateModified: String = "",
    val productionTransferDate: Date? = null,
    val questionCount: Int = 0,
)

data class CategoryColorRGB(
    val blue: Float = 0f,
    val green: Float = 0f,
    val red: Float = 0f,
    val opacity: Float = 0f,
)

