package com.rafalskrzypczyk.translations_mode.presentation.list.ui_models

data class TranslationQuestionUIModel(
    val id: Long,
    val phrase: String,
    val translationsCount: Int,
    val translationsText: String
)
