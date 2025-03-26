package com.rafalskrzypczyk.swipe_mode.presentation.question_details

import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import javax.inject.Inject

class SwipeQuestionDetailsPresenter @Inject constructor(
    private val repository: SwipeModeRepository
) : BasePresenter<SwipeQuestionDetailsContract.View>(), SwipeQuestionDetailsContract.Presenter {

}