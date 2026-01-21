package com.rafalskrzypczyk.translations_mode.domain

import com.rafalskrzypczyk.firestore.data.models.TranslationQuestionDTO
import java.util.Date

fun TranslationQuestionDTO.toDomain() = TranslationQuestion(
    id = id,
    phrase = phrase,
    translations = translations,
    dateCreated = dateCreated,
    dateModified = dateModified
)

fun TranslationQuestion.toDTO() = TranslationQuestionDTO(
    id = id,
    phrase = phrase,
    translations = translations,
    dateCreated = dateCreated,
    dateModified = Date()
)
