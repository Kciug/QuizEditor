package com.rafalskrzypczyk.myapplication
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rafalskrzypczyk.login_screen.LoginHandler
import com.rafalskrzypczyk.myapplication.databinding.AppMainContainerBinding

class MainActivity : AppCompatActivity(), LoginHandler {

    private lateinit var binding: AppMainContainerBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AppMainContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onLoginSuccess() {
        navController.navigate(R.id.nav_editor)
    }
}