package com.billsAplication.presentation.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentSettingsBinding
import com.billsAplication.presentation.chooseCategory.SetLanguageDialog
import com.billsAplication.presentation.mainActivity.MainActivity
import com.billsAplication.utils.Language
import com.billsAplication.utils.StateColorButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import javax.inject.Inject

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = _binding!!

    @Inject
    lateinit var stateColorButton: StateColorButton

    private val component by lazy {
        (requireActivity().application as BillsApplication).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonText()

        switchColorState()

        switchTurnOn()

        setSwitchTheme()

        buttonLanguage()

    }

    companion object {
        private const val LIGHT_THEME = 0
        private const val DARK_THEME = 1
        private const val TYPE_THEME = "themeType"
        private const val CURRANT_LANGUAGE_POS = "currentLanguagePos"
        private const val DEFAULT_POS = 0
        private const val KEY_LANGUAGE_LIST_FRAGMENT = "key_language_from_fragment"
        private const val KEY_CHOSEN_LANGUAGE_LIST_FRAGMENT = "key_SET_language_from_fragment"
        private const val TAG_DIALOG_LANGUAGE = "Dialog_language"
        private const val REQUEST_KEY_LANGUAGE_ITEM = "RequestKey_LANGUAGE_item"
        private const val KEY_LANGUAGE_ITEMS_DIALOG = "key_language_from_dialog"
    }

    private fun buttonLanguage(){
        binding.bLanguage.setOnClickListener {
            setLanguageDialog()
        }
    }

    private fun setLanguageDialog(){
        //Current language
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val pos = sharedPref.getInt(CURRANT_LANGUAGE_POS, DEFAULT_POS)
        //Create dialog
        val dialog = SetLanguageDialog()
        val args = Bundle()
        //sent list to Dialog
        args.putStringArray(KEY_LANGUAGE_LIST_FRAGMENT, listLanguage())
        //sent boolean list of chosen item
        args.putInt(KEY_CHOSEN_LANGUAGE_LIST_FRAGMENT, pos)
        dialog.arguments = args
        //Show dialog
        dialog.show(requireActivity().supportFragmentManager, TAG_DIALOG_LANGUAGE)
        //Receive chosen items from Dialog
        dialog.setFragmentResultListener(REQUEST_KEY_LANGUAGE_ITEM) { requestKey, bundle ->
            val item = bundle.getInt(KEY_LANGUAGE_ITEMS_DIALOG) //get chosen Items
            setLocate(item)
        }
    }

    private fun listLanguage(): Array<out String> {
        var list = arrayOf<String>()
        Language.values().forEach {
            list += it.Name
        }
        return list
    }

    private fun setLocate(position: Int){
        //Save statement of Language in Share preference
        val sharedPref = requireActivity().getPreferences(MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(CURRANT_LANGUAGE_POS, position)
            apply()
        }
        //Set Language
        val locale = Locale(Language.values().get(position).shortName)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        val refresh = Intent(
            requireContext(),
            MainActivity::class.java
        )
        //Update screen
        startActivity(refresh)
    }

    private fun setButtonText(){
        val sharedPref = requireActivity().getPreferences(MODE_PRIVATE)
        val currentLanguagePosition = sharedPref.getInt(CURRANT_LANGUAGE_POS, DEFAULT_POS)
        binding.bLanguage.text = Language.values().get(currentLanguagePosition).Name
        binding.bLanguage.setBackgroundColor(stateColorButton.colorButtons!!)
    }

    private fun switchColorState() {
        val buttonStates = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ), intArrayOf(
                requireActivity().getColor(R.color.default_background), //track unChecked
                stateColorButton.colorButtons!!,
                requireActivity().getColor(R.color.default_background)
            )
        )
        binding.switchTheme.thumbTintList = buttonStates
        binding.switchTheme.trackTintList = buttonStates
    }

    private fun setSwitchTheme() {
        binding.switchTheme.setOnClickListener {
            if (binding.switchTheme.isChecked) {
                setTheme(AppCompatDelegate.MODE_NIGHT_YES, DARK_THEME)
            } else {
                setTheme(AppCompatDelegate.MODE_NIGHT_NO, LIGHT_THEME)
            }
        }
        colorNavBot()
    }

    private fun switchTurnOn() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val typeTheme = sharedPref.getInt(TYPE_THEME, LIGHT_THEME)
        when (typeTheme) {
            LIGHT_THEME -> binding.switchTheme.isChecked = false
            DARK_THEME -> binding.switchTheme.isChecked = true
        }
    }

    private fun colorNavBot() {
        //set color of icon  nav bottom
        (activity as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .itemIconTintList = stateColorButton.stateNavBot!!
        //set color of text nav bottom
        (activity as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .itemTextColor = stateColorButton.stateNavBot!!
        //set color effect
        (activity as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .itemRippleColor = stateColorButton.stateNavBot
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        //Save statement of Theme in Share preference
        val sharedPref = requireActivity().getPreferences(MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(TYPE_THEME, prefsMode)
            apply()
        }
    }

    // Extension method to convert pixels to dp
    fun Int.toDp(context: Context):Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
    ).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}