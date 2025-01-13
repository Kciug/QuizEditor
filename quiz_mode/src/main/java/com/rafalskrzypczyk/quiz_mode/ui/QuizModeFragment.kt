package com.rafalskrzypczyk.quiz_mode.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizModeBinding

class QuizModeFragment : Fragment() {

    private var _binding: FragmentQuizModeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizModeBinding.inflate(inflater, container, false)
        val root = binding.root

        val navView: BottomNavigationView = binding.navQuizModeBottomBar

        //val navController = findNavController()

        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_quiz_mode) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_categories, R.id.navigation_questions
//            )
//        )
        //setupActionBarWithNavController(activity = (requireActivity() as AppCompatActivity), navController = navController)
        navView.setupWithNavController(navController)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}