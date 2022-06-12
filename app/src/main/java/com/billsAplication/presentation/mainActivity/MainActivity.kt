package com.billsAplication.presentation.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.billsAplication.R
import com.billsAplication.presentation.analytics.AnalyticsFragment
import com.billsAplication.presentation.billsList.BillsListFragment
import com.billsAplication.presentation.settings.SettingsFragment
import com.billsAplication.presentation.shopList.ShopListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
    TYPE_EXPENSES = 0
    TYPE_INCOME = 1
    TYPE_CATEGORY = 2
    TYPE_NOTE = 3
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigation()

    }

    fun initBottomNavigation(){
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain) as NavHostFragment
        bottomNavigation.setupWithNavController(navHostFragment.navController)
    }
}