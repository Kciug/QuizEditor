package com.rafalskrzypczyk.quiz_mode.data

data class AnswerDTO(
    val id: Int = -1,
    val answerText: String = "",
    @field:JvmField
    val isCorrect: Boolean = false,
)