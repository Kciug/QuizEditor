package com.rafalskrzypczyk.home.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.data_statistics.DataStatistics
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.nav_handling.DrawerNavigationHandler
import com.rafalskrzypczyk.home.R
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

        with(binding) {
            btnContinueWork.setOnClickListener { presenter.onContinueWork() }
            with(statisticsDev.statQuizMode){
                tvModeName.text = getString(R.string.statistics_title_quiz_mode)
                tvTypeFirstName.text = getString(R.string.statistics_title_quiz_mode_categories)
                tvTypeSecondName.text = getString(R.string.statistics_title_quiz_mode_questions)
            }
            statisticsDev.statSwipeMode.tvModeName.text = getString(R.string.statistics_title_swipe_mode)
            statisticsDev.statCalculationsMode.tvModeName.text = getString(R.string.statistics_title_calculations_mode)
            statisticsDev.statScenariosMode.tvModeName.text = getString(R.string.statistics_title_scenarios_mode)
        }
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
        with(binding.statisticsDev) {
            statDbName.text = statistics.dataBaseName
            with(statQuizMode) {
                tvFirstElementsCount.text = String.format(statistics.quizModeStatistics.numberOfCategories.toString())
                tvSecondElementsCount.text = String.format(statistics.quizModeStatistics.numberOfQuestions.toString())
            }
            statSwipeMode.tvElementsCount.text = String.format(statistics.swipeQuizModeStatistics.toString())
            statCalculationsMode.tvElementsCount.text = String.format(statistics.calculationsModeStatistics.toString())
            statScenariosMode.tvElementsCount.text = String.format(statistics.scenariosModeStatistics.toString())
        }
    }

    override fun displayLoading() {

    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}