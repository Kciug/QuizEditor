package com.rafalskrzypczyk.core.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.utils.UITextHelpers

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val searchBar: EditText
    private val clearButton: ImageButton
    private val elementsCount: TextView
    private val elementsCountLabel: TextView

    private var onTextChanged: ((String) -> Unit)? = null
    private var onClearClick: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_search_bar, this, true)
        searchBar = findViewById(R.id.search_field)
        clearButton = findViewById(R.id.button_clear)
        elementsCount = findViewById(R.id.tv_elements_count)
        elementsCountLabel = findViewById(R.id.tv_label_elements_count)


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

    fun setElementsCount(count: Int){
        elementsCount.text = String.format(count.toString())
        elementsCountLabel.text = UITextHelpers.provideDeclinedNumberText(
            count,
            context.getString(R.string.elements_count_label_singular),
            context.getString(R.string.elements_count_label_few),
            context.getString(R.string.elements_count_label_many)
        )
    }
}