package com.rafalskrzypczyk.login_screen

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.rafalskrzypczyk.login_screen.databinding.ActivityLoginBinding
import com.rafalskrzypczyk.login_screen.databinding.FragmentLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveTaskToBack(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add the back pressed callback
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    fun handleLoginButtonClick() {
        setResult(RESULT_OK)
        backPressedCallback.isEnabled = false
        onBackPressedDispatcher.onBackPressed()
    }
}
