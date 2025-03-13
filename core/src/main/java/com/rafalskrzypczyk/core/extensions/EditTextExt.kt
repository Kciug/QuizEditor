package com.rafalskrzypczyk.core.extensions

import android.text.InputType
import android.widget.EditText

fun EditText.setupMultilineWithIMEAction(imeAction: Int){
    imeOptions = imeAction
    setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
}