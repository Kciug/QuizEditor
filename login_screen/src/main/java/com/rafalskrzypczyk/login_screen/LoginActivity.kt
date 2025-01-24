package com.rafalskrzypczyk.login_screen

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.rafalskrzypczyk.login_screen.databinding.FragmentLoginBinding

class LoginActivity : AppCompatActivity() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveTaskToBack(true)
        }
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        binding.loginButton.setOnClickListener {
            setResult(RESULT_OK)
            backPressedCallback.isEnabled = false
            onBackPressedDispatcher.onBackPressed()
        }
    }

}