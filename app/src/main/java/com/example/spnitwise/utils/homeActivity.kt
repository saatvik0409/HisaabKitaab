package com.example.spnitwise.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.spnitwise.ExpensesFragment
import com.example.spnitwise.FriendsFragment
import com.example.spnitwise.HomeFragment
import com.example.spnitwise.ProfileFragment
import com.example.spnitwise.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class homeActivity : AppCompatActivity() {

    private lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        bottomNav = findViewById(R.id.bottom_nav)

        supportFragmentManager.commit {
            replace<HomeFragment>(R.id.nav_host_fragment)
            setReorderingAllowed(true)
            addToBackStack("Initiation")
        }

        bottomNav.setOnItemSelectedListener{item ->
            if (DISABLE_BOTTOM_MENU_LISTENER) {
                return@setOnItemSelectedListener true
            }
            when(item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.commit {
                        replace<HomeFragment>(R.id.nav_host_fragment)
                        addToBackStack("Home")
                    }
                }

                R.id.friendsFragment ->{
                    supportFragmentManager.commit {
                        replace<FriendsFragment>(R.id.nav_host_fragment)
                        addToBackStack("Friends")
                    }
                }

                R.id.expensesFragment ->{
                    supportFragmentManager.commit {
                        replace<ExpensesFragment>(R.id.nav_host_fragment)
                        addToBackStack("Expenses")
                    }
                }
                R.id.profileFragment -> {
                    supportFragmentManager.commit {
                        replace<ProfileFragment>(R.id.nav_host_fragment)
                        addToBackStack("Profile")
                    }
                }
            }
            true
        }

//        val navController = findNavController(R.id.nav_host_fragment)
//
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.homeFragment, R.id.searchFragment, R.id.profileFragment
//            )
//        )
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

    }



}