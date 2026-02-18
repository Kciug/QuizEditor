package com.rafalskrzypczyk.core.domain.models

enum class GameMode(val value: String) {
    QUIZ("main"),
    SWIPE("swipe"),
    TRANSLATION("translations"),
    CEM("cem");

    companion object {
        fun fromString(value: String): GameMode {
            return values().find { it.value == value } ?: QUIZ
        }
    }
}
