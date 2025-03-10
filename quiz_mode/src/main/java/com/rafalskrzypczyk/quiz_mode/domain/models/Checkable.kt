package com.rafalskrzypczyk.quiz_mode.domain.models

data class Checkable(
    val id: Long,
    val title: String,
    var isChecked: Boolean,
    val isLocked: Boolean
)
