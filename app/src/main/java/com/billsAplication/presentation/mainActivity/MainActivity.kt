package com.billsAplication.presentation.mainActivity

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.billsAplication.R
import com.billsAplication.databinding.ActivityMainBinding
import com.billsAplication.utils.Currency
import com.billsAplication.utils.CurrentCurrency
import com.billsAplication.utils.InterfaceMainActivity
import com.billsAplication.utils.Language
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

/*
    TYPE_EXPENSES = 0
    TYPE_INCOME = 1
    TYPE_CATEGORY = 2
    TYPE_NOTE = 3
 */

class MainActivity : AppCompatActivity(), InterfaceMainActivity {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocate()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdMob()

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

    private fun initAdMob(){
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewBanner.loadAd(adRequest)
    }

    fun initBottomNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
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

    override fun navBottom(): BottomNavigationView {
        return binding.bottomNavigation
    }

    override fun onResume() {
        super.onResume()
        binding.adViewBanner.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.adViewBanner.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adViewBanner.destroy()
    }

    override fun onStart() {
        setTheme()
        super.onStart()
    }
}