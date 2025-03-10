package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class AnswerDTO(
    val id: Long = -1,
    val answerText: String = "",
    @field:JvmField
    val isCorrect: Boolean = false,
    val dateCreated: Date = Date(),
    val dateModified: String = ""
)