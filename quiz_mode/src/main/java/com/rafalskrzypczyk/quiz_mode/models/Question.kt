package com.rafalskrzypczyk.quiz_mode.models

data class Question(
    val id: Int,
    val text: String,
    val answers: List<Answer>
)