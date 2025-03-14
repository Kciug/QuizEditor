package com.rafalskrzypczyk.home.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.home.databinding.FragmentHomeScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>(FragmentHomeScreenBinding::inflate),
HomeScreenContract.View {

    @Inject
    lateinit var presenter: HomeScreenContract.Presenter

    private var startWorkGuideView: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttach(this)
        presenter.onViewCreated()
    }

    override fun onViewBound() {
        super.onViewBound()

        binding.btnContinueWork.setOnClickListener { presenter.onContinueWork() }
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        super.onDestroyView()
    }

    override fun displayUserName(name: String) {
        binding.tvUserName.text = name
    }

    override fun setStartWorkGuide() {
        binding.btnContinueWork.visibility = View.GONE
        binding.tvContinueWorkMessage.visibility = View.GONE

        if(startWorkGuideView == null) {
            val startWorkGuideStub = binding.stubStartWorkGuide
            startWorkGuideView = startWorkGuideStub.inflate()
        }
    }

    override fun navigateToDestination(destination: Int) {
        findNavController().navigate(destination)
    }

    override fun displayStatistics(statistics: List<DataStatisticsUIModel>) {

    }

    override fun displayLoading() {

    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}