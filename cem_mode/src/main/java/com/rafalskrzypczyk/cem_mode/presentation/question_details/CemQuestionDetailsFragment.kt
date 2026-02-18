package com.rafalskrzypczyk.cem_mode.presentation.question_details

import com.rafalskrzypczyk.cem_mode.databinding.FragmentCemQuestionDetailsBinding
import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.base.BasePresenter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

interface CemQuestionDetailsContract {
    interface View : BaseContract.View
    interface Presenter : BaseContract.Presenter<View>
}

class CemQuestionDetailsPresenter @Inject constructor(
    private val repository: CemModeRepository
) : BasePresenter<CemQuestionDetailsContract.View>(), CemQuestionDetailsContract.Presenter

@AndroidEntryPoint
class CemQuestionDetailsFragment :
    BaseBottomSheetFragment<FragmentCemQuestionDetailsBinding, CemQuestionDetailsContract.View, CemQuestionDetailsContract.Presenter>(
        FragmentCemQuestionDetailsBinding::inflate
    ), CemQuestionDetailsContract.View {

    override fun displayLoading() {}
    override fun displayError(message: String) {}

    override fun onViewBound() {
        super.onViewBound()
    }
}
