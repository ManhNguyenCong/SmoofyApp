package com.example.smoothieshopapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.smoothieshopapp.data.model.User
import com.example.smoothieshopapp.data.network.SmoothieApi
import com.example.smoothieshopapp.data.repository.SmoothieRepository
import com.example.smoothieshopapp.data.repository.UserRepository
import com.example.smoothieshopapp.databinding.ActivityMainBinding
import com.example.smoothieshopapp.ui.loginscreen.LoginActivity
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModel
import com.example.smoothieshopapp.ui.moothiescreen.viewmodel.SmoothieViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel: SmoothieViewModel by viewModels {
        SmoothieViewModelFactory(UserRepository(), SmoothieRepository())
    }

    private var userCurrent: User? = null
        set(value) {
            field = value
            setNavView(field)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set navigation view onclick
        setNavViewItemOnClick()

        setBottomNavViewItemOnClick()

        SmoothieApi.firebaseAuth.addAuthStateListener {
            it.currentUser?.uid?.let { uid ->
                viewModel.getUserByUID(
                    uid,
                    onSuccess = { user ->
                        userCurrent = user
                    },
                    onFailure = { mgs ->
                        Log.d("Test Smoothie", "onCreate: $mgs")
                    })
            } ?: setNavView(null)
        }
    }


    private fun setNavView(user: User?) {
        val navView = findViewById<NavigationView>(R.id.navigationView)
        val tvUserName = navView?.getHeaderView(0)?.findViewById<TextView>(R.id.userName)
        if (user == null) {
            navView.menu.findItem(R.id.loginMenuItem).title = "Login"
            tvUserName?.text = ""
            return
        }

        tvUserName?.text = user.name ?: ""
        navView.menu.findItem(R.id.loginMenuItem).title = "Logout"
        setNavViewItemOnClick()
    }

    /**
     * This function is used to set event onclick of menu item in navigation view
     */
    private fun setNavViewItemOnClick() {
        //Todo set onclick menu item in navigation view
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profileMenuItem -> {
                    Toast.makeText(this, "Profile isn't completed now", Toast.LENGTH_SHORT).show()
                }

                R.id.loginMenuItem -> {
                    // TODO check current user, if true logout
                    if (userCurrent != null) {
                        SmoothieApi.firebaseAuth.signOut()
                    }
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            // Close navigation view
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    /**
     * This function is used to set navigate to bottom navigation view
     *
     */
    private fun setBottomNavViewItemOnClick() {
        // Get bottom navigation view
        val bottomNavView =
            binding.mainContent.findViewById<BottomNavigationView>(R.id.bottomNavView) ?: return
        bottomNavView.setupWithNavController(navController)

        bottomNavView.menu.findItem(R.id.detailSmoothieFragment).isEnabled = false
        bottomNavView.setOnItemSelectedListener {
            navController.navigate(it.itemId)
            true
        }
    }
}