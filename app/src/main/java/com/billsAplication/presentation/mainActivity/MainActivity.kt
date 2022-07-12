package com.billsAplication.presentation.mainActivity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import com.billsAplication.utils.Language
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

/*
    TYPE_EXPENSES = 0
    TYPE_INCOME = 1
    TYPE_CATEGORY = 2
    TYPE_NOTE = 3
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setLocate()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigation()

    }

    companion object{
        private const val TYPE_THEME = "themeType"
        private const val LIGHT_THEME = 0
        private const val DARK_THEME = 1
        private const val CURRANT_LANGUAGE_POS = "currentLanguagePos"
        private const val DEFAULT_POS = 0
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

    private fun setLocate(){
        //Get statement of Language in Share preference
        val sharedPref = this.getPreferences(MODE_PRIVATE)
        val position = sharedPref.getInt(CURRANT_LANGUAGE_POS, DEFAULT_POS)
        //Set Language
        val locale = Locale(Language.values().get(position).shortName)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
        val refresh = Intent(
            this,
            MainActivity::class.java
        )
        //Update screen
//        startActivity(refresh)
    }

    override fun onStart() {
        setTheme()
        super.onStart()
    }
}