package com.rafalskrzypczyk.core.domain.models

enum class GameMode(val value: String) {
    QUIZ("QUIZ_MODE"),
    SWIPE("SWIPE_MODE"),
    TRANSLATION("TRANSLATIONS_MODE"),
    CEM("CEM_MODE");

    companion object {
        fun fromString(value: String): GameMode {
            return values().find { it.value == value } ?: QUIZ
        }
    }
}
