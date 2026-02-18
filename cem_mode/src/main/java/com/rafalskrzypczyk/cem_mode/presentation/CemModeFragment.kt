package com.rafalskrzypczyk.cem_mode.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rafalskrzypczyk.cem_mode.R
import com.rafalskrzypczyk.cem_mode.databinding.FragmentCemModeBinding

class CemModeFragment : Fragment() {

    private var _binding: FragmentCemModeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCemModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.navCemModeBottomBar) { v, insets ->
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, 0)
            insets
        }

        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_cem_mode) as NavHostFragment
        navController = navHostFragment.navController

        binding.navCemModeBottomBar.setupWithNavController(navController)
    }

    fun navigateToQuestions(bundle: Bundle) {
        navController.navigate(R.id.navigation_cem_questions, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
