package com.rafalskrzypczyk.quizeditor
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.rafalskrzypczyk.auth.domain.UserManager
import com.rafalskrzypczyk.login_screen.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userManager: UserManager

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK){
                startMainAppActivity()
            }
        }

        if(userManager.isUserLogged()){
            startMainAppActivity()
        } else {
            startLoginActivity()
        }
    }

    private fun startMainAppActivity(){
        val intent = Intent(this, ApplicationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        loginLauncher.launch(intent)
    }
}