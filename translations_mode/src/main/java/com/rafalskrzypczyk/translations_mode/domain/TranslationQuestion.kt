package com.rafalskrzypczyk.translations_mode.domain

import com.rafalskrzypczyk.core.base.Identifiable
import com.rafalskrzypczyk.core.extensions.generateId
import java.util.Date

data class TranslationQuestion(
    override val id: Long,
    val phrase: String,
    val translations: List<String>,
    val dateCreated: Date,
    val dateModified: Date
) : Identifiable {
    companion object {
        fun new(phrase: String, translations: List<String>) = TranslationQuestion(
            id = Long.generateId(),
            phrase = phrase,
            translations = translations,
            dateCreated = Date(),
            dateModified = Date()
        )
    }
}
