package com.billsAplication.presentation.mainActivity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.billsAplication.R
import com.billsAplication.databinding.ActivityMainBinding
import com.billsAplication.utils.*
import com.billsAplication.utils.Currency
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/*
    TYPE_EXPENSES = 0
    TYPE_INCOME = 1
    TYPE_CATEGORY_EXPENSES = 2
    TYPE_CATEGORY_INCOME = 4
    TYPE_NOTE = 3
    TYPE_NOTE_RECEIVE = 123
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
        //if we receive note string from other app
        intentActionSendText()

    }

    companion object {
        private const val TYPE_THEME = "themeType"
        private const val LIGHT_THEME = 0
        private const val DARK_THEME = 1
        private const val CURRANT_LANGUAGE_POS = "currentLanguagePos"
        private const val DEFAULT_POS = 0
        private const val CURRANT_CURRENCY_TYPE = "currentCurrencyType"
        private const val CURRANT_CURRENCY_POS = "currentCurrencyPos"
        private const val DEFAULT_TYPE = false
        private const val KEY_NOTE_RECEIVE = "key_note_receive"
        private const val TYPE_NOTE_RECEIVE = "type_note_receive"

    }

    private fun intentActionSendText() {
        val intent = intent
        if (intent.action == Intent.ACTION_SEND) {
            if ("text/plain" == intent.type) {
                handleSendText(intent)
            }
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            // Update UI to reflect text being shared
            setSharePref(it)
            if (intent.action == Intent.ACTION_SEND) {
                intent.action = null
            }
        }
    }

    private fun setSharePref(note: String) {
        //Save statement of Currency in Share preference
        val sharedPref = getPreferences(MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(TYPE_NOTE_RECEIVE, true)
            putString(KEY_NOTE_RECEIVE, note)
            apply()
        }
    }

    private fun initAdMob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewBanner.loadAd(adRequest)
    }

    fun initBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
    }

    //Theme
    fun setTheme() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val typeTheme = sharedPref.getInt(TYPE_THEME, LIGHT_THEME)
        when (typeTheme) {
            LIGHT_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun setLocate() {
        //Get statement of Language in Share preference
        val sharedPref = this.getPreferences(MODE_PRIVATE)
        val position = sharedPref.getInt(CURRANT_LANGUAGE_POS, DEFAULT_POS)
        //Set Language
        if (position != DEFAULT_POS) {
            val locale = Locale(Language.values().get(position).shortName)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            this.resources.updateConfiguration(config, this.resources.displayMetrics)
        } else {
            val locale = Locale(
                ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration())
                    .get(0)?.language
            )
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            this.resources.updateConfiguration(config, this.resources.displayMetrics)
        }
    }

    private fun setCurrency() {
        //Get statement of Currency in Share preference
        val sharedPref = this.getPreferences(MODE_PRIVATE)
        val type = sharedPref.getBoolean(CURRANT_CURRENCY_TYPE, DEFAULT_TYPE)
        val curPos = sharedPref.getInt(CURRANT_CURRENCY_POS, Currency.United_States.ordinal)
        if (type) {
            //currency
            CurrentCurrency.currency = Currency.values().get(curPos).symbol
        } else {
            //currency
            CurrentCurrency.currency = Currency.values().get(curPos).code
        }
    }

    override fun navBottom(): BottomNavigationView {
        return binding.bottomNavigation
    }

    override suspend fun splash() {
        if (binding.splash.visibility == View.VISIBLE) {
            withContext(Dispatchers.Main) {
                delay(1000)
//                binding.splash.visibility = View.GONE
                FadeOutView().invoke(binding.splash)
                binding.cardViewContainer.isEnabled = true
                binding.cardViewNavBottom.isEnabled = true
            }
        }
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