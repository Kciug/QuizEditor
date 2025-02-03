package com.rafalskrzypczyk.quiz_mode.domain.models

import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus
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