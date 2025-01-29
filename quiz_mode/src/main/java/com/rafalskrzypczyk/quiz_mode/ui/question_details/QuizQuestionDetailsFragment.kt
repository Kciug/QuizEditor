package com.rafalskrzypczyk.quiz_mode.ui.question_details

import android.os.Bundle
import android.view.View
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizQuestionDetailsBinding

class QuizQuestionDetailsFragment(
    val bundle: Bundle? = null,
    onDismiss: () -> Unit,
) : BaseBottomSheetFragment<FragmentQuizQuestionDetailsBinding>(
    FragmentQuizQuestionDetailsBinding::inflate,
    onDismiss
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}