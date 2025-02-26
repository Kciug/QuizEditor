package com.rafalskrzypczyk.quiz_mode.presentation.categories_list

import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel

data class QuestionUIModel (
    val id: Int,
    var text: String,
    val answersCount: Int,
    val linkedCategories: List<SimpleCategoryUIModel>
)

fun Question.toUIModel(linkedCategories: List<SimpleCategoryUIModel>) = QuestionUIModel(
    id = id,
    text = text,
    answersCount = answers.count(),
    linkedCategories = linkedCategories
)