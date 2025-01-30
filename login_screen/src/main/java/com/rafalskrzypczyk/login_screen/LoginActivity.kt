package com.rafalskrzypczyk.login_screen

import androidx.activity.OnBackPressedCallback
import com.rafalskrzypczyk.core.base.BaseCompatActivity
import com.rafalskrzypczyk.login_screen.databinding.ActivityLoginBinding

class LoginActivity : BaseCompatActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveTaskToBack(true)
        }
    }

    override fun onViewBound() {
        super.onViewBound()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    fun handleLoginButtonClick() {
        setResult(RESULT_OK)
        backPressedCallback.isEnabled = false
        onBackPressedDispatcher.onBackPressed()
    }
}
