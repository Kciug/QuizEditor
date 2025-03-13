package com.rafalskrzypczyk.quiz_mode.presentation.questions_list.ui_models

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.rafalskrzypczyk.quiz_mode.R

sealed class QuestionValidationMessage(
    @StringRes val message: Int,
    @DrawableRes val icon: Int,
    @ColorRes val color: Int
) {
    object OK : QuestionValidationMessage(R.string.question_validation_ok, com.rafalskrzypczyk.core.R.drawable.ic_check_24, com.rafalskrzypczyk.core.R.color.green)
    object MissingAnswers : QuestionValidationMessage(R.string.question_validation_missing_answers, com.rafalskrzypczyk.core.R.drawable.ic_warning_amber_24, com.rafalskrzypczyk.core.R.color.red)
    object NoCorrectAnswers : QuestionValidationMessage(R.string.question_validation_no_correct_answer, com.rafalskrzypczyk.core.R.drawable.ic_warning_amber_24, com.rafalskrzypczyk.core.R.color.red)
}