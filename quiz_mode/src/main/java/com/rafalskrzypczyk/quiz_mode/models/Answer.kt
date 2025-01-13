package com.rafalskrzypczyk.quiz_mode.models

data class Answer(
    val id: Int,
    val answerText: String,
    val isCorrect: Boolean
)
