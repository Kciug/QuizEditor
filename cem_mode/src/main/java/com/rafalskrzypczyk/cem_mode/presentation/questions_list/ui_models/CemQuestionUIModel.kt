package com.rafalskrzypczyk.cem_mode.presentation.questions_list.ui_models

import com.rafalskrzypczyk.cem_mode.domain.models.CemQuestion
import com.rafalskrzypczyk.core.domain.models.QuestionValidationMessage
import com.rafalskrzypczyk.core.presentation.ui_models.SimpleCategoryUIModel

data class CemQuestionUIModel (
    val id: Long,
    var text: String,
    val answersCount: Int,
    val linkedCategories: List<SimpleCategoryUIModel>,
    val validationMessage: QuestionValidationMessage
)

fun CemQuestion.toUIModel(linkedCategories: List<SimpleCategoryUIModel>) = CemQuestionUIModel(
    id = id,
    text = text,
    answersCount = answers.count(),
    linkedCategories = linkedCategories,
    validationMessage =
    if(answers.isEmpty()) QuestionValidationMessage.MissingAnswers
    else if (answers.none { it.isCorrect }) QuestionValidationMessage.NoCorrectAnswers
    else QuestionValidationMessage.OK
)
