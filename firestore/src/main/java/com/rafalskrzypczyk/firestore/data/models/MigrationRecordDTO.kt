package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class MigrationRecordDTO(
    val id: String = "",
    val mode: String = "",
    val sourceCollection: String = "",
    val targetCollection: String = "",
    val itemCount: Int = 0,
    val itemDetails: List<String> = emptyList(),
    val performedBy: String = "",
    val date: Date = Date()
)
