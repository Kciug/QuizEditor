package com.rafalskrzypczyk.firestore.data.models

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

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
) {
    val isUpToDate: Boolean
        get() {
            if (productionTransferDate == null) return false
            val modifiedDateParsed = try {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateModified)
            } catch (e: Exception) {
                null
            } ?: return false
            
            // Item is up to date if production migration happened at the same time or after modification
            return productionTransferDate.after(modifiedDateParsed) || productionTransferDate == modifiedDateParsed
        }
}

data class CategoryColorRGB(
    val blue: Float = 0f,
    val green: Float = 0f,
    val red: Float = 0f,
    val opacity: Float = 0f,
)

