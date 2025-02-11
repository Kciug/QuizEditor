package com.rafalskrzypczyk.quiz_mode.data

data class CategoryDTO(
    val id: String,
    val title: String,
    val description: String,
    val questionAmount: Int,
    val status: String,
)
