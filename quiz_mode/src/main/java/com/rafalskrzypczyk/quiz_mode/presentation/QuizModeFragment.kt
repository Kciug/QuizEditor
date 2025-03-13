package com.rafalskrzypczyk.quiz_mode.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizModeBinding
import com.rafalskrzypczyk.quiz_mode.domain.DataUpdateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizModeFragment : BaseFragment<FragmentQuizModeBinding>(FragmentQuizModeBinding::inflate) {

    @Inject
    lateinit var dataUpdateManager: DataUpdateManager

    private lateinit var navController: NavController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataUpdateManager.initialize()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_quiz_mode) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigateView: BottomNavigationView = binding.navQuizModeBottomBar
        bottomNavigateView.setupWithNavController(navController)
    }

    override fun onDestroyView() {
        dataUpdateManager.clear()
        super.onDestroyView()
    }
}