package com.rafalskrzypczyk.quiz_mode.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.domain.DataUpdateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizModeFragment : Fragment() {

    @Inject
    lateinit var dataUpdateManager: DataUpdateManager

    private lateinit var navController: NavController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataUpdateManager.initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz_mode, container, false)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_quiz_mode) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigateView : BottomNavigationView = view.findViewById(R.id.nav_quiz_mode_bottom_bar)
        bottomNavigateView.setupWithNavController(navController)

        return view.rootView
    }

    override fun onDestroyView() {
        dataUpdateManager.clear()
        super.onDestroyView()
    }
}


//        ViewCompat.setOnApplyWindowInsetsListener(binding.navQuizModeBottomBar) { view, insets ->
//            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            view.setPadding(0, 0, 0, systemBarsInsets.bottom)
//            ViewCompat.onApplyWindowInsets(view, insets)
//        }

//        ViewCompat.setOnApplyWindowInsetsListener(binding.navHostFragmentQuizMode.rootView) { v, insets ->
//            @Suppress("SENSELESS_COMPARISON")
//            if(view == null || !isAdded) return@setOnApplyWindowInsetsListener insets
//
//            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
//            binding.navQuizModeBottomBar.visibility = if (imeVisible) View.GONE else View.VISIBLE
//            ViewCompat.onApplyWindowInsets(v, insets)
//        }