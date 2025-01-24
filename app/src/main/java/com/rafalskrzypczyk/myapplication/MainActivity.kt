package com.rafalskrzypczyk.myapplication
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rafalskrzypczyk.login_screen.LoginHandler
import com.rafalskrzypczyk.myapplication.databinding.AppMainContainerBinding

class MainActivity : AppCompatActivity(), LoginHandler {

    private lateinit var binding: AppMainContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AppMainContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onLoginSuccess() {
        val intent = Intent(this, ApplicationActivity::class.java)
        startActivity(intent)
    }
}