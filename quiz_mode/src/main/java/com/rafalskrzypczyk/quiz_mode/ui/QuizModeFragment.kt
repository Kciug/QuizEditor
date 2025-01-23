package com.rafalskrzypczyk.quiz_mode.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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

        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_quiz_mode) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}