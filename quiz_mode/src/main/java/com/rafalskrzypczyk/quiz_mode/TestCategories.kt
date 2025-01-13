package com.rafalskrzypczyk.quiz_mode

import com.rafalskrzypczyk.quiz_mode.models.Category

object TestCategories {
    val categories = mutableListOf(
        Category(
            id = 1,
            title = "Science",
            description = "Questions about physics, chemistry, and biology.",
            questionAmount = 20
        ),
        Category(
            id = 2,
            title = "History",
            description = "Explore historical events and figures.",
            questionAmount = 15
        ),
        Category(
            id = 3,
            title = "Geography",
            description = "Test your knowledge about countries and landscapes.",
            questionAmount = 25
        ),
        Category(
            id = 4,
            title = "Technology",
            description = "Questions about modern and ancient technologies.",
            questionAmount = 10
        ),
        Category(
            id = 5,
            title = "Sports",
            description = "Trivia about various sports and athletes.",
            questionAmount = 30
        )
    )
}
