package com.rafalskrzypczyk.home.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.data_statistics.DataStatistics
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.nav_handling.DrawerNavigationHandler
import com.rafalskrzypczyk.home.databinding.FragmentHomeScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>(FragmentHomeScreenBinding::inflate),
HomeScreenContract.View {

    @Inject
    lateinit var presenter: HomeScreenContract.Presenter

    private var startWorkGuideView: View? = null
    private var activityDrawerNavigationHandler: DrawerNavigationHandler? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttach(this)
        presenter.onViewCreated()
        activityDrawerNavigationHandler = activity as? DrawerNavigationHandler
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
        activityDrawerNavigationHandler?.navigateToDestination(destination)
    }

    override fun displayStatistics(statistics: DataStatistics) {
        val statisticsView = binding.statisticsDev
        with(binding.statisticsDev){
            statisticsView.statDbName.text = statistics.dataBaseName
            with(statisticsView.statQuizMode){
                tvModeName.text = "QuizMode"
                tvTypeFirstName.text = "Kategorie"
                tvTypeSecondName.text = "Liczba pytań"
                tvFirstElementsCount.text = statistics.quizModeStatistics.numberOfCategories.toString()
                tvSecondElementsCount.text = statistics.quizModeStatistics.numberOfQuestions.toString()
            }
        }
    }

    override fun displayLoading() {

    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}