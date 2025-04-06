package com.rafalskrzypczyk.core.animations

import android.view.View
import androidx.core.view.isVisible
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeVisible

object QuizEditorAnimations {
    const val ANIMATION_DURATION = 200L
    const val ANIMATION_DELAY = 50L

    fun animateScaleOut(view: View, onEnd: (() -> Unit)? = null) {
        if(view.isVisible.not()) return
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(ANIMATION_DURATION)
            .withEndAction {
                view.apply {
                    makeGone()
                    scaleX = 1f
                    scaleY = 1f
                    alpha = 1f
                }
                onEnd?.invoke()
            }
            .start()
    }

    fun animateScaleIn(view: View, onEnd: (() -> Unit)? = null) {
        if(view.isVisible) return
        view.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
            makeVisible()
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
            makeVisible()
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
            makeVisible()
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