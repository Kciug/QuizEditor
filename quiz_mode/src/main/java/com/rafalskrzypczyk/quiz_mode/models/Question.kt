package com.rafalskrzypczyk.quiz_mode.models

import java.util.Date

data class Question(
    val id: Int,
    var text: String,
    var answers: MutableList<Answer>,
    val creationDate: Date,
    val createdBy: String
)