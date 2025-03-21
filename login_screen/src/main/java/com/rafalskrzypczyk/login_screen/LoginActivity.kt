package com.rafalskrzypczyk.login_screen

import com.rafalskrzypczyk.core.base.BaseCompatActivity
import com.rafalskrzypczyk.login_screen.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseCompatActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate), SuccessLoginHandler {

//    private val backPressedCallback = object : OnBackPressedCallback(true) {
//        override fun handleOnBackPressed() {
//            if(!findNavController(R.id.nav_host_fragment_login).popBackStack())
//                finish()
//        }
//    }

    override fun onViewBound() {
        super.onViewBound()
        //onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onSuccessLogin() {
        setResult(RESULT_OK)
        //backPressedCallback.isEnabled = false
        //onBackPressedDispatcher.onBackPressed()
        finish()
    }
}
