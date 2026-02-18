package com.rafalskrzypczyk.core.domain.models

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.rafalskrzypczyk.core.R

sealed class QuestionValidationMessage(
    @StringRes val message: Int,
    @DrawableRes val icon: Int,
    @ColorRes val color: Int
) {
    object OK : QuestionValidationMessage(R.string.question_validation_ok, R.drawable.ic_check_24, R.color.green)
    object MissingAnswers : QuestionValidationMessage(R.string.question_validation_missing_answers, R.drawable.ic_warning_amber_24, R.color.red)
    object NoCorrectAnswers : QuestionValidationMessage(R.string.question_validation_no_correct_answer, R.drawable.ic_warning_amber_24, R.color.red)
}
