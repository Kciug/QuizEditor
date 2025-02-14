package com.rafalskrzypczyk.quiz_mode.data

import java.util.Date

data class QuestionDTO(
    val id: Int = -1,
    val questionText: String = "",
    val answersObject: List<AnswerDTO> = emptyList(),
    val categoryIDs: List<Int> = emptyList(),
    val dateCreated: Date = Date(),
    val answersCount: Int = 0,
    val correctAnswersCount: Int = 0
)
