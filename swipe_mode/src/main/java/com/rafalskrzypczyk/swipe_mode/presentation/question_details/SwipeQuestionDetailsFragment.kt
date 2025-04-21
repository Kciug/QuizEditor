package com.rafalskrzypczyk.swipe_mode.presentation.question_details

import android.animation.ObjectAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.extensions.setupMultilineWithIMEAction
import com.rafalskrzypczyk.core.utils.KeyboardController
import com.rafalskrzypczyk.swipe_mode.R
import com.rafalskrzypczyk.swipe_mode.databinding.FragmentSwipeQuestionDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwipeQuestionDetailsFragment :
    BaseBottomSheetFragment<FragmentSwipeQuestionDetailsBinding, SwipeQuestionDetailsContract.View, SwipeQuestionDetailsContract.Presenter>(
        FragmentSwipeQuestionDetailsBinding::inflate
    ), SwipeQuestionDetailsContract.View {

    private lateinit var keyboardController: KeyboardController

    private var currentPickerSelected: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardController = KeyboardController(requireContext())
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()

        with(binding){
            inputQuestion.setupMultilineWithIMEAction(EditorInfo.IME_ACTION_DONE)
            inputQuestion.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //presenter.updateQuestionText(inputQuestion.text.toString())
                    if(inputQuestion.text.isNotEmpty()) keyboardController.hideKeyboard(inputQuestion)
                    true
                } else false
            }
            inputQuestion.addTextChangedListener(
                afterTextChanged = {
                    presenter.updateQuestionText(it.toString())
                }
            )

            changeCorrectSelection(null, correctnessPicker.btnAnswerUnknown, false)

            with(correctnessPicker){
                btnAnswerFalse.setOnClickListener { changeCorrectSelection(false, btnAnswerFalse) }
                btnAnswerTrue.setOnClickListener { changeCorrectSelection(true, btnAnswerTrue) }
                btnAnswerUnknown.setOnClickListener { changeCorrectSelection(null, btnAnswerUnknown) }
            }

            navbar.buttonClose.setOnClickListener { dismiss() }
            navbar.buttonSave.setOnClickListener {
                presenter.saveNewQuestion(inputQuestion.text.toString(), currentPickerSelected)
            }
            btnAddNext.setOnClickListener {
                presenter.saveAndOpenNewQuestion(inputQuestion.text.toString(), currentPickerSelected)
            }
        }
    }

    override fun displayQuestionDetails(questionText: String, isCorrect: Boolean?) {
        with(binding) {
            inputQuestion.setText(questionText)
            changeCorrectSelection(isCorrect, when (isCorrect) {
                    true -> correctnessPicker.btnAnswerTrue
                    false -> correctnessPicker.btnAnswerFalse
                    else -> correctnessPicker.btnAnswerUnknown
            }, false)

            if(loading.root.isVisible){
                loading.root.makeGone()
                content.makeVisible()
            }
        }
    }

    override fun displayCreatedDetails(dateCreated: String) {
        binding.creationDetails.labelCreationDate.text = dateCreated
        QuizEditorAnimations.animateScaleIn(binding.creationDetails.root)
        binding.navbar.buttonSave.makeGone()
    }

    override fun replaceWithNewQuestion() {
        with(binding) {
            QuizEditorAnimations.animateScaleOut(binding.root) {
                inputQuestion.setText("")
                changeCorrectSelection(null, correctnessPicker.btnAnswerUnknown, false)
                creationDetails.root.makeInvisible()
                QuizEditorAnimations.animateScaleIn(binding.root)
                binding.navbar.buttonSave.makeVisible()
            }
        }
    }

    override fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun openKeyboard() {
        binding.inputQuestion.requestFocus()
        keyboardController.showKeyboardWithDelay(binding.inputQuestion)
    }

    override fun displayLoading() {
        binding.content.makeInvisible()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }

    private fun changeCorrectSelection(isCorrect: Boolean?, target: View, animated: Boolean = true) {
        currentPickerSelected = isCorrect
        presenter.updateIsCorrect(isCorrect)
        animateBackgroundTo(target, when (isCorrect) {
            true -> com.rafalskrzypczyk.core.R.color.green
            false -> com.rafalskrzypczyk.core.R.color.red
            else -> R.color.answer_neutral
        }, animated)
    }

    private fun animateBackgroundTo(target: View, targetColor: Int, animated: Boolean) {
        val pickerLayout = binding.correctnessPicker.root
        val constraintSet = ConstraintSet()
        constraintSet.clone(pickerLayout)

        with(binding.correctnessPicker) {
            constraintSet.connect(answerIndicator.id, ConstraintSet.START, target.id, ConstraintSet.START)
            constraintSet.connect(answerIndicator.id, ConstraintSet.END, target.id, ConstraintSet.END)
            constraintSet.connect(answerIndicator.id, ConstraintSet.TOP, target.id, ConstraintSet.TOP)
            constraintSet.connect(answerIndicator.id, ConstraintSet.BOTTOM, target.id, ConstraintSet.BOTTOM)
        }

        if(animated) TransitionManager.beginDelayedTransition(pickerLayout)

        constraintSet.applyTo(pickerLayout)

        val drawable = binding.correctnessPicker.answerIndicator.background.mutate() as GradientDrawable

        if(animated){
            ObjectAnimator.ofArgb(
                drawable,
                "color",
                drawable.color?.defaultColor!!,
                requireContext().getColor(targetColor)
            ).apply {
                duration = 300
                start()
            }
        } else drawable.setColor(requireContext().getColor(targetColor))
    }
}
