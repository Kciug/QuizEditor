package com.rafalskrzypczyk.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.Companion.formatDate(date: Date) : String {
    return SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(date)
}