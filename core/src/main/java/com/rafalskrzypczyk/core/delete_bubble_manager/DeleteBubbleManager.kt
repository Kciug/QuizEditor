package com.rafalskrzypczyk.core.delete_bubble_manager

import android.animation.ObjectAnimator
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.rafalskrzypczyk.core.R

class DeleteBubbleManager(private val context: Context) {
    fun showDeleteBubble(view: View, onDelete: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val bubbleView = inflater.inflate(R.layout.bubble_delete, view.rootView as ViewGroup, false)

        bubbleView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val popupWindow = PopupWindow(
            bubbleView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val location = IntArray(2)
        view.getLocationOnScreen(location)

        val popupX = location[0] + (view.width / 2) - bubbleView.measuredWidth / 2
        val popupY = location[1] + (view.height / 2) - bubbleView.measuredHeight / 2

        popupWindow.animationStyle = android.R.style.Animation_Dialog
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0.35f).apply {
            duration = 150
            start()
        }
        popupWindow.setOnDismissListener {
            ObjectAnimator.ofFloat(view, "alpha", 0.35f, 1f).apply {
                duration = 150
                start()
            }
        }

        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, popupX, popupY)

        bubbleView.setOnClickListener {
            onDelete()
            popupWindow.dismiss()
        }
    }
}