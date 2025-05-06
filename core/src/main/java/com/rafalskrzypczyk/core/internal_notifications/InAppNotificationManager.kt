package com.rafalskrzypczyk.core.internal_notifications

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import javax.inject.Inject

class InAppNotificationManager @Inject constructor(
    private val activity: AppCompatActivity
) {
    private var notificationView: View? = null
    private var isVisible = false
    private var progressAnimator: ValueAnimator? = null

    private val countdownTime = 4000L

    fun show(title: String, message: String, @DrawableRes iconRes: Int? = null, onClick: (() -> Unit)? = null) {
        if(isVisible) return

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.layout_internal_notification, rootView, false)

        val titleTextView = view.findViewById<TextView>(R.id.tv_notification_title)
        val messageTextView = view.findViewById<TextView>(R.id.tv_notification_message)
        val iconImageView = view.findViewById<ImageView>(R.id.iv_notification_icon)
        val timerProgressBar = view.findViewById<ProgressBar>(R.id.pb_notification_timer)

        iconImageView.setImageDrawable(AppCompatResources.getDrawable(activity, iconRes  ?: R.drawable.ic_circle_notifications))
        titleTextView.text = title
        messageTextView.text = message

        view.setOnClickListener {
            hide()
            onClick?.invoke()
        }

        rootView.addView(view)
        notificationView = view
        isVisible = true

        QuizEditorAnimations.animateScaleIn(view)

        progressAnimator = ValueAnimator.ofInt(100, 0).apply {
            duration = countdownTime
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                timerProgressBar.progress = progress
            }
            doOnEnd { hide() }
            start()
        }
    }

    private fun hide() {
        notificationView?.let {
            QuizEditorAnimations.animateScaleOut(it) {
                (notificationView?.parent as? ViewGroup)?.removeView(notificationView)
                notificationView = null
                isVisible = false
                progressAnimator?.cancel()
                progressAnimator = null
            }
        }
    }
}