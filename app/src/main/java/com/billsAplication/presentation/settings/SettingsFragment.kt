package com.billsAplication.presentation.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.billsAplication.R
import com.billsAplication.databinding.FragmentBillsListBinding
import com.billsAplication.databinding.FragmentSettingsBinding
import com.billsAplication.presentation.mainActivity.MainActivity
import com.billsAplication.presentation.mainActivity.MainActivity.Companion.TYPE_THEME

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
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

        switchTurnOn()

        setSwitchTheme()
    }

    private fun setSwitchTheme() {
        binding.switchTheme.setOnClickListener {
            if (binding.switchTheme.isChecked) {
                setTheme(AppCompatDelegate.MODE_NIGHT_YES, DARK_THEME)
            } else {
                setTheme(AppCompatDelegate.MODE_NIGHT_NO, LIGHT_THEME)
            }
        }
    }

    companion object {
        const val LIGHT_THEME = 0
        const val DARK_THEME = 1
        const val TYPE_THEME = "themeType"
    }

    private fun switchTurnOn(){
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val typeTheme = sharedPref.getInt(MainActivity.TYPE_THEME, MainActivity.LIGHT_THEME)
        when (typeTheme) {
            LIGHT_THEME -> binding.switchTheme.isChecked = false
            DARK_THEME -> binding.switchTheme.isChecked = true
        }
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        //Save statement of Theme in Share preference
        val sharedPref = requireActivity().getPreferences(MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt(TYPE_THEME, prefsMode)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}