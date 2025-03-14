package com.rafalskrzypczyk.core.extensions

import android.text.InputType
import android.widget.EditText

fun EditText.setupMultilineWithIMEAction(imeAction: Int){
    imeOptions = imeAction
    inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_CLASS_TEXT
    setHorizontallyScrolling(false)
    maxLines = Int.MAX_VALUE
}