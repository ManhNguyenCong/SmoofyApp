package com.example.smoothieshopapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.smoothieshopapp.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set navigation view onclick
        setNavViewItemOnClick()

        setBottomNavViewItemOnClick()

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

                R.id.messageMenuItem -> {
                    Toast.makeText(this, "Messages isn't completed now", Toast.LENGTH_SHORT).show()
                }

                R.id.promotionsMenuItem -> {
                    Toast.makeText(this, "Promotions isn't completed now", Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.favoritesMenuItem -> {
                    Toast.makeText(this, "Favorites isn't completed now", Toast.LENGTH_SHORT).show()
                }

                R.id.categoriesMenuItem -> {
                    Toast.makeText(this, "Categories isn't completed now", Toast.LENGTH_SHORT)
                        .show()
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
        // Get current user
        val user = Firebase.auth.currentUser
        // Set user name in navigation view
        user?.also {
            Firebase.database.reference.child("users/${user.uid}/name").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.value.toString()
                        findViewById<NavigationView>(R.id.navigationView)
                            ?.getHeaderView(0)
                            ?.findViewById<TextView>(R.id.userName)
                            ?.text = name.ifEmpty {
                            "User name"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TAG", "onCancelled: " + error.toException())
                    }

                }
            )

        }


        // Get bottom navigation view
        val bottomNavView =
            binding.mainContent.findViewById<BottomNavigationView>(R.id.bottomNavView) ?: return

        // Set don't select menu item: detailSmoothieFragment
        bottomNavView.menu.findItem(R.id.detailSmoothieFragment).isEnabled = false
        // Setup nav controller for bottom nav controller
        // Notes: id of menu item must be the same as id of fragment in nav_graph
        bottomNavView.setupWithNavController(navController)
        // When use setupWithNavController, navController don't navigate to
        // new fragment, it can born a bug, so need navigate it
        bottomNavView.setOnItemSelectedListener {
            navController.navigate(it.itemId)
            true
        }
    }
}