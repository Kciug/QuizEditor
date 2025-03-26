package com.rafalskrzypczyk.swipe_mode.presentation.question_details

import android.animation.ObjectAnimator
import android.graphics.drawable.GradientDrawable
import android.transition.TransitionManager
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.swipe_mode.R
import com.rafalskrzypczyk.swipe_mode.databinding.FragmentSwipeQuestionDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwipeQuestionDetailsFragment :
    BaseBottomSheetFragment<FragmentSwipeQuestionDetailsBinding, SwipeQuestionDetailsContract.View, SwipeQuestionDetailsContract.Presenter>(
        FragmentSwipeQuestionDetailsBinding::inflate
    ), SwipeQuestionDetailsContract.View {

    override fun onViewBound() {
        super.onViewBound()

        with(binding){
            btnAnswerFalse.setOnClickListener { animateBackgroundTo(btnAnswerFalse, com.rafalskrzypczyk.core.R.color.red) }
            btnAnswerTrue.setOnClickListener { animateBackgroundTo(btnAnswerTrue, com.rafalskrzypczyk.core.R.color.green) }
            btnAnswerUnknown.setOnClickListener { animateBackgroundTo(btnAnswerUnknown, R.color.answer_neutral) }
        }
    }

    override fun displayLoading() {
        TODO("Not yet implemented")
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }

    private fun animateBackgroundTo(target: View, targetColor: Int) {
        val constraintLayout = binding.root
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintSet.connect(binding.answerIndicator.id, ConstraintSet.START, target.id, ConstraintSet.START)
        constraintSet.connect(binding.answerIndicator.id, ConstraintSet.END, target.id, ConstraintSet.END)
        constraintSet.connect(binding.answerIndicator.id, ConstraintSet.TOP, target.id, ConstraintSet.TOP)
        constraintSet.connect(binding.answerIndicator.id, ConstraintSet.BOTTOM, target.id, ConstraintSet.BOTTOM)

        TransitionManager.beginDelayedTransition(constraintLayout)
        constraintSet.applyTo(constraintLayout)

        val drawable = binding.answerIndicator.background.mutate() as GradientDrawable

        ObjectAnimator.ofArgb(
            drawable,
            "color",
            drawable.color?.defaultColor!!,
            requireContext().getColor(targetColor)
        ).apply {
            duration = 300
            start()
        }
    }
}