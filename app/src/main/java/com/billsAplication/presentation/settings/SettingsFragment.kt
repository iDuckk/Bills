package com.billsAplication.presentation.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentSettingsBinding
import com.billsAplication.presentation.mainActivity.MainActivity
import com.billsAplication.utils.StateColorButton
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        switchColorState()

        switchTurnOn()

        setSwitchTheme()
    }

    private fun switchColorState() {
        val buttonStates = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ), intArrayOf(
                Color.BLUE,
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

    companion object {
        const val LIGHT_THEME = 0
        const val DARK_THEME = 1
        const val TYPE_THEME = "themeType"
    }

    private fun switchTurnOn() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val typeTheme = sharedPref.getInt(MainActivity.TYPE_THEME, MainActivity.LIGHT_THEME)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}