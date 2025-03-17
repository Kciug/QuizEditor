package com.rafalskrzypczyk.core.animations

import android.view.View

object QuizEditorScaleInOutAnimation {
    const val ANIMATION_DURATION = 300L

    fun animateScaleOut(view: View, onEnd: (() -> Unit)?) {
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(ANIMATION_DURATION)
            .withEndAction {
                view.visibility = View.GONE
                onEnd?.invoke()
            }
            .start()
    }

    fun animateScaleIn(view: View, onEnd: (() -> Unit)?) {
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(ANIMATION_DURATION)
            .withStartAction {
                view.scaleX = 0f
                view.scaleY = 0f
                view.alpha = 0f
                view.visibility = View.VISIBLE
            }
            .withEndAction {
                onEnd?.invoke()
            }
            .start()
    }

    fun replaceViewsWithScaleInOut(view1: View, view2: View) {
        animateScaleOut(view1) {
            animateScaleIn(view2, null)
        }
    }
}