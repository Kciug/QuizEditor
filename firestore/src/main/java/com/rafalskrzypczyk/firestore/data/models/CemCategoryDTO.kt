package com.rafalskrzypczyk.firestore.data.models

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class CemCategoryDTO(
    val id: Long = -1,
    val title: String = "",
    val subtitle: String? = null,
    val questionIDs: List<Long> = emptyList(),
    val subcategoryIDs: List<Long> = emptyList(),
    var parentCategoryID: Long? = null,
    val status: String = "",
    val color: CategoryColorRGB? = null,
    val free: Boolean = false,
    val dateCreated: Date = Date(),
    val dateModified: String = "",
    val productionTransferDate: Date? = null,
    var questionsCount: Int = 0,
    var subcategoriesCount: Int = 0,
) {
    val isUpToDate: Boolean
        get() {
            if (productionTransferDate == null) return false
            val modifiedDateParsed = try {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateModified)
            } catch (e: Exception) {
                null
            } ?: return false
            return productionTransferDate.after(modifiedDateParsed) || productionTransferDate == modifiedDateParsed
        }
}
