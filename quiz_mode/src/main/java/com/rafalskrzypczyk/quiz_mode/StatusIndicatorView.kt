package com.rafalskrzypczyk.quiz_mode

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class StatusIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val colorIndicator: ImageView
    private val statusText: TextView
    private val colorIndicatorBackground: GradientDrawable

    init {
        LayoutInflater.from(context).inflate(R.layout.view_status_indicator, this, true)

        colorIndicator = findViewById(R.id.category_status_color)
        statusText = findViewById(R.id.category_status_indicator)

        colorIndicatorBackground = colorIndicator.background as GradientDrawable

        setColorAndText(context.getColor(R.color.status_draft), context.getString(R.string.status_draft))
    }

    fun setColorAndText(color: Int, text: String){
        colorIndicatorBackground.setColor(color)
        statusText.setTextColor(color)

        statusText.text = text
    }
}