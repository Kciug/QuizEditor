package com.rafalskrzypczyk.core.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window

abstract class BaseDialog(context: Context) : Dialog(context) {
    val widthRatio = 0.9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * widthRatio).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}