package com.billsAplication.presentation.mainActivity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
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

    companion object{
        const val TYPE_THEME = "themeType"
        const val LIGHT_THEME = 0
        const val DARK_THEME = 1
    }

    fun initBottomNavigation(){
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain) as NavHostFragment
        bottomNavigation.setupWithNavController(navHostFragment.navController)
    }

    //Theme
    fun setTheme(){
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val typeTheme = sharedPref.getInt(TYPE_THEME, LIGHT_THEME)
        when (typeTheme) {
            LIGHT_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun onStart() {
        setTheme()
        super.onStart()
    }
}