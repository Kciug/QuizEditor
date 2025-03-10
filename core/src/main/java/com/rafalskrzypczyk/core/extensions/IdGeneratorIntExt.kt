package com.rafalskrzypczyk.core.extensions

import java.util.Calendar

fun Long.Companion.generateId() : Long {
    val timestamp = Calendar.getInstance().timeInMillis
    val randomPart = (0..100).random()
    return timestamp + randomPart
}