package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class CemQuestionDTO(
    val id: Long = -1,
    val questionText: String = "",
    val answers: List<CemAnswerDTO> = emptyList(),
    val categoryIDs: List<Long> = emptyList(),
    val dateCreated: Date = Date(),
    val dateModified: String = "",
)

data class CemAnswerDTO(
    val id: Long = -1,
    val answerText: String = "",
    @field:JvmField
    val isCorrect: Boolean = false,
    val dateCreated: Date = Date(),
    val dateModified: String = ""
)
