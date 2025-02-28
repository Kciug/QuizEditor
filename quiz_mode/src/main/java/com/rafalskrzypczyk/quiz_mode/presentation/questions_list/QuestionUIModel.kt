package com.rafalskrzypczyk.quiz_mode.presentation.questions_list

import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.ui_models.SimpleCategoryUIModel

data class QuestionUIModel (
    val id: Int,
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
    if(answers.isEmpty()) QuestionValidationMessage.MISSING_ANSWERS
    else if (answers.none { it.isCorrect }) QuestionValidationMessage.NO_CORRECT_ANSWER
    else QuestionValidationMessage.OK
)