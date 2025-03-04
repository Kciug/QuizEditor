package com.rafalskrzypczyk.core.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window

abstract class BaseDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        window?.attributes?.windowAnimations = com.rafalskrzypczyk.core.R.style.FadeInOutAnimation
    }
}