package com.rafalskrzypczyk.login_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rafalskrzypczyk.login_screen.databinding.FragmentLoginBinding

class LoginScreenFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginHander: LoginHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        loginHander = context as LoginHandler

        binding.loginButton.setOnClickListener {
            loginHander.onLoginSuccess()
        }

        return binding.root
    }

}