package com.rafalskrzypczyk.core.utils

import android.graphics.Color

class TextColorContrastHelper {
    companion object{
        fun getContrastingTextColor(backgroundColor: Int): Int {
            val red = (backgroundColor shr 16) and 0xFF
            val green = (backgroundColor shr 8) and 0xFF
            val blue = backgroundColor and 0xFF

            val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0

            return if (luminance > 0.5) Color.BLACK else Color.WHITE
        }
    }
}