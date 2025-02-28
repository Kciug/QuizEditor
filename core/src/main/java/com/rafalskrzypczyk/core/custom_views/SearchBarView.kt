package com.rafalskrzypczyk.core.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.rafalskrzypczyk.core.R

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val searchBar: EditText
    private val clearButton: ImageButton

    private var onTextChanged: ((String) -> Unit)? = null
    private var onClearClick: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_search_bar, this, true)
        searchBar = findViewById(R.id.search_field)
        clearButton = findViewById(R.id.button_clear)

        clearButton.setOnClickListener {
            searchBar.text.clear()
            onClearClick?.invoke()
        }

        searchBar.addTextChangedListener(
            afterTextChanged = {
                onTextChanged?.invoke(it.toString())
                clearButton.visibility = if (it.isNullOrEmpty()) GONE else VISIBLE
            }
        )
    }

    fun setOnTextChanged(onChange: (String) -> Unit) {
        onTextChanged = onChange
    }

    fun setOnClearClick(onClick: () -> Unit) {
        onClearClick = onClick
    }
}