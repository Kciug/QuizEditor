package com.rafalskrzypczyk.quiz_mode.data

import android.graphics.Color
import com.rafalskrzypczyk.quiz_mode.domain.CategoryStatus
import java.util.Date

data class CategoryDTO(
    val id: Int = -1,
    val title: String = "",
    val subtitle: String = "",
    val questionIDs: List<Int> = emptyList(),
    val status: CategoryStatus = CategoryStatus.DRAFT,
    val color: Int = Color.TRANSPARENT,
    val dateCreated: Date = Date(),
    val questionAmount: Int,
)

