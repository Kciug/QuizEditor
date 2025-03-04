package com.rafalskrzypczyk.login_screen

import androidx.activity.OnBackPressedCallback
import com.rafalskrzypczyk.core.base.BaseCompatActivity
import com.rafalskrzypczyk.login_screen.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseCompatActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate), SuccessLoginHandler {

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveTaskToBack(true)
        }
    }

    override fun onViewBound() {
        super.onViewBound()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onSuccessLogin() {
        setResult(RESULT_OK)
        backPressedCallback.isEnabled = false
        onBackPressedDispatcher.onBackPressed()
    }
}
