package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class SwipeQuestionDTO(
    val id: Long = -1,
    val text: String = "",
    @field:JvmField
    val isCorrect: Boolean = false,
    val dateCreated: Date = Date(),
    val dateModified: Date = Date()
)