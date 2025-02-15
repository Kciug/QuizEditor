package com.rafalskrzypczyk.quiz_mode.domain.models

data class Checkable(
    val id: Int,
    val title: String,
    var isChecked: Boolean,
    val isLocked: Boolean
)
