package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class TranslationQuestionDTO(
    val id: Long = -1,
    val phrase: String = "",
    val translations: List<String> = emptyList(),
    val dateCreated: Date = Date(),
    val dateModified: Date = Date(),
    val productionTransferDate: Date? = null
)