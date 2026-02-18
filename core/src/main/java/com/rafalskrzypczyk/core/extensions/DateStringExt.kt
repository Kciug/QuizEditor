package com.rafalskrzypczyk.core.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.Companion.formatDate(date: Date) : String {
    return SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(date)
}

fun Date.formatToDataDate(): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)
}