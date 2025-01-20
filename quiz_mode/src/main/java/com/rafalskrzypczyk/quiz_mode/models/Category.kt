package com.rafalskrzypczyk.quiz_mode.models

import com.rafalskrzypczyk.quiz_mode.CategoryStatus
import java.time.LocalDateTime
import java.util.Date

data class Category (
    val id: Int,
    var title: String,
    var description: String,
    var questionAmount: Int = 0,
    val creationDate: Date,

    var color: Long = 0xFF2196F3,
    var status: CategoryStatus = CategoryStatus.DRAFT
)