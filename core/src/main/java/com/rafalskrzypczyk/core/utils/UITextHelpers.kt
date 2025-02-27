package com.rafalskrzypczyk.core.utils

import android.graphics.Color

class UITextHelpers {
    companion object{
        fun getContrastingTextColor(backgroundColor: Int): Int {
            val red = (backgroundColor shr 16) and 0xFF
            val green = (backgroundColor shr 8) and 0xFF
            val blue = backgroundColor and 0xFF

            val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0

            return if (luminance > 0.5) Color.BLACK else Color.WHITE
        }

        fun provideDeclinedNumberText(
            number: Int,
            singularForm: String,
            fewForm: String,
            manyForm: String
        ): String {
            val lastDigit = number % 10
            val secondLastDigit = (number / 10) % 10

            return when {
                number == 1 -> singularForm
                lastDigit in 2..4 && secondLastDigit != 1 -> fewForm
                else -> manyForm
            }
        }
    }
}