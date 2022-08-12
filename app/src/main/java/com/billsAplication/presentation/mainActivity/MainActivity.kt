package com.billsAplication.presentation.mainActivity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
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
import com.billsAplication.utils.Currency
import com.billsAplication.utils.CurrentCurrency
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

        setCurrency()
    }

    companion object{
        private const val TYPE_THEME = "themeType"
        private const val LIGHT_THEME = 0
        private const val DARK_THEME = 1
        private const val CURRANT_LANGUAGE_POS = "currentLanguagePos"
        private const val DEFAULT_POS = 0
        private const val CURRANT_CURRENCY_TYPE = "currentCurrencyType"
        private const val CURRANT_CURRENCY_POS = "currentCurrencyPos"
        private const val DEFAULT_TYPE = false

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
        if(position != DEFAULT_POS) {
            val locale = Locale(Language.values().get(position).shortName)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            this.resources.updateConfiguration(config, this.resources.displayMetrics)
        }
        else{
            val locale = Locale(ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0)?.language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            this.resources.updateConfiguration(config, this.resources.displayMetrics)
        }
    }

    private fun setCurrency(){
        //Get statement of Currency in Share preference
        val sharedPref = this.getPreferences(MODE_PRIVATE)
        val type = sharedPref.getBoolean(CURRANT_CURRENCY_TYPE, DEFAULT_TYPE)
        val curPos = sharedPref.getInt(CURRANT_CURRENCY_POS, Currency.United_States.ordinal)
        if(type){
            //currency
            CurrentCurrency.currency = Currency.values().get(curPos).symbol
        }else{
            //currency
            CurrentCurrency.currency = Currency.values().get(curPos).code
        }
    }

    override fun onStart() {
        setTheme()
        super.onStart()
    }
}