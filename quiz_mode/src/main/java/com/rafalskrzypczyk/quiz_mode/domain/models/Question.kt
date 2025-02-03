package com.rafalskrzypczyk.quiz_mode.domain.models

import android.icu.util.Calendar
import com.rafalskrzypczyk.core.utils.generateId
import java.util.Date

data class Question(
    val id: Int,
    var text: String,
    val answers: MutableList<Answer> = mutableListOf(),
    val creationDate: Date,
    val createdBy: String,
    val linkedCategories: MutableList<Int> = mutableListOf()
) {
    companion object {
        fun new(text: String): Question {
            return Question(
                id = Int.generateId(),
                text = text,
                creationDate = Calendar.getInstance().time,
                createdBy = "Kurwa Chuj"
            )
        }
    }
}