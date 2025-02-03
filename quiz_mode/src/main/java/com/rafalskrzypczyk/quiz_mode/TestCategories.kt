package com.rafalskrzypczyk.quiz_mode

import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.utils.CategoryStatus
import java.util.Date

object TestCategories {
    val cat = mutableListOf<Category>()

    val categories = mutableListOf(
        Category(
            id = 1,
            title = "Science",
            description = "Questions about physics, chemistry, and biology.",
            questionAmount = 20,
            color = 0xFF2196F3,
            creationDate = Date(),
            status = CategoryStatus.DRAFT
        ),
        Category(
            id = 2,
            title = "History",
            description = "Explore historical events and figures.",
            questionAmount = 15,
            color = 0xFF4CAF50,
            creationDate = Date(),
            status = CategoryStatus.IN_PROGRESS
        ),
        Category(
            id = 3,
            title = "Geography",
            description = "Test your knowledge about countries and landscapes.",
            questionAmount = 25,
            color = 0xFFFFC107,
            creationDate = Date(),
            status = CategoryStatus.DRAFT
        ),
        Category(
            id = 4,
            title = "Technology",
            description = "Questions about modern and ancient technologies.",
            questionAmount = 10,
            color = 0xFF9C27B0,
            creationDate = Date(),
            status = CategoryStatus.DRAFT
        ),
        Category(
            id = 5,
            title = "Sports",
            description = "Trivia about various sports and athletes.",
            questionAmount = 30,
            color = 0xFFEF5350,
            creationDate = Date(),
            status = CategoryStatus.DONE
        )
    )
}
