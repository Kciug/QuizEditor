package com.rafalskrzypczyk.quiz_mode.models

data class Category (
    val id: Int,
    val title: String,
    val description: String,
    val questionAmount: Int,
)