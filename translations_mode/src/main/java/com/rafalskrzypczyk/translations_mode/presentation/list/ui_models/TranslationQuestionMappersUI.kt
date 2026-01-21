package com.rafalskrzypczyk.translations_mode.presentation.list.ui_models

import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.translations_mode.R
import com.rafalskrzypczyk.translations_mode.domain.TranslationQuestion

fun TranslationQuestion.toUIModel(resourceProvider: ResourceProvider): TranslationQuestionUIModel {
    val count = translations.size
    val text = if (count > 0) {
        resourceProvider.getString(R.string.translations_count_format, count)
    } else {
        resourceProvider.getString(R.string.no_translations)
    }
    return TranslationQuestionUIModel(
        id = id,
        phrase = phrase,
        translationsCount = count,
        translationsText = text
    )
}
