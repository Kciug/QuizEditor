package com.rafalskrzypczyk.core.utils

import java.util.Calendar

fun Int.Companion.generateId() : Int {
    val timestamp = Calendar.getInstance().timeInMillis
    val randomPart = (0..100).random()
    return timestamp.toInt() + randomPart
}