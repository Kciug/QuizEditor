package com.rafalskrzypczyk.quiz_mode.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentQuizModeBinding
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuizCategoriesFragment
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuizQuestionsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizModeFragment : BaseFragment<FragmentQuizModeBinding>(FragmentQuizModeBinding::inflate) {

    private val bottomBarFragmentManager by lazy { childFragmentManager }
    private val categoriesFragment = QuizCategoriesFragment()
    private val questionsFragment = QuizQuestionsFragment()
    private var activeFragment: Fragment = categoriesFragment

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bottomBarFragmentManager.beginTransaction()
//            .add(R.id.nav_host_fragment_quiz_mode, categoriesFragment, "categories")
//            .add(R.id.nav_host_fragment_quiz_mode, questionsFragment, "questions")
//            .hide(questionsFragment)
//            .commit()
//        navView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.navigation_categories -> switchFragment(categoriesFragment)
//                R.id.navigation_questions -> switchFragment(questionsFragment)
//            }
//            true
//        }
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_quiz_mode) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigateView: BottomNavigationView = binding.navQuizModeBottomBar
        bottomNavigateView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_categories, R.id.navigation_questions)
        )
    }

    private fun switchFragment(targetFragment: Fragment ) {
        if(activeFragment == targetFragment) return
        bottomBarFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(targetFragment)
            .commit()
        activeFragment = targetFragment
    }
}