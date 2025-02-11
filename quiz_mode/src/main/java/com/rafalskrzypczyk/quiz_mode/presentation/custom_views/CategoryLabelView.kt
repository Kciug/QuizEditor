package com.rafalskrzypczyk.quiz_mode.presentation.custom_views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.core.utils.TextColorContrastHelper

class CategoryLabelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val colorIndicator: ImageView
    private val statusText: TextView
    private val colorIndicatorBackground: GradientDrawable

    init {
        LayoutInflater.from(context).inflate(R.layout.view_category_label, this, true)

        colorIndicator = findViewById(R.id.category_color)
        statusText = findViewById(R.id.category_name)

        colorIndicatorBackground = colorIndicator.background as GradientDrawable

        setColorAndText(context.getColor(com.rafalskrzypczyk.core.R.color.red), context.getString(com.rafalskrzypczyk.core.R.string.text_placeholder_short))
    }

    fun setColorAndText(color: Int, text: String){
        colorIndicatorBackground.setColor(color)
        statusText.setTextColor(TextColorContrastHelper.Companion.getContrastingTextColor(color))

        statusText.text = text
    }
}