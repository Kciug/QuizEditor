package com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models

import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel

data class QuestionUIModel (
    val id: Long,
    var text: String,
    val answersCount: Int,
    val linkedCategories: List<SimpleCategoryUIModel>,
    val validationMessage: QuestionValidationMessage
)

fun Question.toUIModel(linkedCategories: List<SimpleCategoryUIModel>) = QuestionUIModel(
    id = id,
    text = text,
    answersCount = answers.count(),
    linkedCategories = linkedCategories,
    validationMessage =
    if(answers.isEmpty()) QuestionValidationMessage.MissingAnswers
    else if (answers.none { it.isCorrect }) QuestionValidationMessage.NoCorrectAnswers
    else QuestionValidationMessage.OK
)