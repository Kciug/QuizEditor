package com.rafalskrzypczyk.cem_mode.presentation.category_details

import com.rafalskrzypczyk.cem_mode.databinding.FragmentCemCategoryDetailsBinding
import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.base.BasePresenter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

interface CemCategoryDetailsContract {
    interface View : BaseContract.View
    interface Presenter : BaseContract.Presenter<View>
}

class CemCategoryDetailsPresenter @Inject constructor(
    private val repository: CemModeRepository
) : BasePresenter<CemCategoryDetailsContract.View>(), CemCategoryDetailsContract.Presenter

@AndroidEntryPoint
class CemCategoryDetailsFragment :
    BaseBottomSheetFragment<FragmentCemCategoryDetailsBinding, CemCategoryDetailsContract.View, CemCategoryDetailsContract.Presenter>(
        FragmentCemCategoryDetailsBinding::inflate
    ), CemCategoryDetailsContract.View {

    override fun displayLoading() {}
    override fun displayError(message: String) {}

    override fun onViewBound() {
        super.onViewBound()
    }
}
