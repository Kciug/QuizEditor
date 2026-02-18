package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class CemCategoryDTO(
    val id: Long = -1,
    val title: String = "",
    val subtitle: String? = null,
    val questionIDs: List<Long> = emptyList(),
    val subcategoryIDs: List<Long> = emptyList(),
    val parentCategoryID: Long? = null,
    val status: String = "",
    val color: CategoryColorRGB? = null,
    val free: Boolean = false,
    val dateCreated: Date = Date(),
    val dateModified: String = "",
    val productionTransferDate: Date? = null,
    val questionCount: Int = 0,
    val subcategoryCount: Int = 0,
)
