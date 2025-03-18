package com.rafalskrzypczyk.core.animations

import android.view.View

object QuizEditorAnimations {
    const val ANIMATION_DURATION = 200L
    const val ANIMATION_DELAY = 50L

    fun animateScaleOut(view: View, onEnd: (() -> Unit)? = null) {
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

    fun animateScaleIn(view: View, onEnd: (() -> Unit)? = null) {
        view.apply {
            view.scaleX = 0f
            view.scaleY = 0f
            view.alpha = 0f
            view.visibility = View.VISIBLE
        }

        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(ANIMATION_DURATION)
            .withEndAction { onEnd?.invoke() }
            .start()
    }

    fun animateExpandFromTop(view: View, onEnd: (() -> Unit)? = null) {
        view.apply {
            scaleY = 0f
            alpha = 0f
            visibility = View.VISIBLE
        }

        view.animate()
            .scaleY(1f)
            .alpha(1f)
            .setDuration(ANIMATION_DURATION)
            .setStartDelay(ANIMATION_DELAY)
            .withEndAction { onEnd?.invoke() }
            .start()
    }

    fun animateExpandFromLeft(view: View, onEnd: (() -> Unit)? = null) {
        view.apply {
            scaleX = 0f
            alpha = 0f
            visibility = View.VISIBLE
        }

        view.animate()
            .scaleX(1f)
            .alpha(1f)
            .setDuration(ANIMATION_DURATION)
            .setStartDelay(ANIMATION_DELAY)
            .withEndAction { onEnd?.invoke() }
            .start()
    }

    fun animateReplaceScaleOutIn(view1: View, view2: View) {
        animateScaleOut(view1) {
            animateScaleIn(view2)
        }
    }

    fun animateReplaceScaleOutExpandFromTop(view1: View, view2: View) {
        animateScaleOut(view1) {
            animateExpandFromTop(view2)
        }

    }
}