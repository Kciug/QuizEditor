package com.rafalskrzypczyk.login_screen

import com.rafalskrzypczyk.core.base.BaseCompatActivity
import com.rafalskrzypczyk.login_screen.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseCompatActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate), SuccessLoginHandler {

    override fun onViewBound() {
        super.onViewBound()
    }

    override fun onSuccessLogin() {
        setResult(RESULT_OK)
        finish()
    }
}
