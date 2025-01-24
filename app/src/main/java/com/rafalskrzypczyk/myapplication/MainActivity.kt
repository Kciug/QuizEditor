package com.rafalskrzypczyk.myapplication
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.rafalskrzypczyk.login_screen.LoginActivity

class MainActivity : AppCompatActivity() {

    companion object {
        var isLogged = false
    }

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK){
                isLogged = true
                startMainAppActivity()
            }
        }

        if(isLogged){
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