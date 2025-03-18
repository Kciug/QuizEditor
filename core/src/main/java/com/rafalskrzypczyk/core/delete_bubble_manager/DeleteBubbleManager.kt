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
    fun showDeleteBubble(anchorView: View, onDelete: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val bubbleView = inflater.inflate(R.layout.bubble_delete, anchorView.rootView as ViewGroup, false)

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
        anchorView.getLocationOnScreen(location)

        val popupX = location[0] + (anchorView.width / 2) - bubbleView.measuredWidth / 2
        val popupY = location[1] + (anchorView.height / 2) - bubbleView.measuredHeight / 2

        popupWindow.animationStyle = android.R.style.Animation_Dialog
        ObjectAnimator.ofFloat(anchorView, "alpha", 1f, 0.35f).apply {
            duration = 150
            start()
        }
        popupWindow.setOnDismissListener {
            ObjectAnimator.ofFloat(anchorView, "alpha", 0.35f, 1f).apply {
                duration = 150
                start()
            }
        }

        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, popupX, popupY)

        bubbleView.setOnClickListener {
            onDelete()
            popupWindow.dismiss()
        }
    }
}