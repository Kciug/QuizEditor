package com.rafalskrzypczyk.firestore.data.models

import java.util.Date

data class QuestionDTO(
    val id: Long = -1,
    val questionText: String = "",
    val answers: List<AnswerDTO> = emptyList(),
    val categoryIDs: List<Long> = emptyList(),
    val dateCreated: Date = Date(),
    val dateModified: String = "",
)
