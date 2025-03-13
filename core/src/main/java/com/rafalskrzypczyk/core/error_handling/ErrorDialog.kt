package com.rafalskrzypczyk.core.error_handling

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.base.BaseDialog

class ErrorDialog(
    context: Context,
    private val errorMessage: String
) : BaseDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_error)

        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val messageTextView: TextView = findViewById(R.id.tv_error_message)
        val buttonClose: Button = findViewById(R.id.btn_dismiss)

        messageTextView.text = errorMessage
        buttonClose.setOnClickListener { dismiss() }
    }
}