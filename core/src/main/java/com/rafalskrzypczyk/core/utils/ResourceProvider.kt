package com.rafalskrzypczyk.core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(resId: Int): String = context.getString(resId)

    fun getString(resId: Int, vararg formatArgs: Any): String = context.getString(resId, *formatArgs)

    fun getQuantityString(resId: Int, quantity: Int, vararg formatArgs: Any): String =
        context.resources.getQuantityString(resId, quantity, *formatArgs)

    fun getColor(resId: Int): Int = context.getColor(resId)

    fun getDrawable(resId: Int): Drawable? = AppCompatResources.getDrawable(context, resId)
}