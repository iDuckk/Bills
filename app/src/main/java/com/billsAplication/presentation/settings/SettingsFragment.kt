package com.billsAplication.presentation.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentSettingsBinding
import com.billsAplication.presentation.chooseCategory.SetLanguageDialog
import com.billsAplication.presentation.mainActivity.MainActivity
import com.billsAplication.utils.*
import com.billsAplication.utils.Currency
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.util.*
import javax.inject.Inject

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = _binding!!

    @Inject
    lateinit var stateColorButton: StateColorButton
    @Inject
    lateinit var exportDatabaseFile: ExportDatabaseFile
    @Inject
    lateinit var importDatabaseFile: ImportDatabaseFile


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

        setDefaultValues()

        setButtonText()

        switchColorState()

        radioButtonsColorState()

        switchTurnOn()

        setSwitchTheme()

        buttonLanguage()

        spinnerCurrency()

        radioButtonsCurrency()

        backup()

    }

    companion object {
        private const val LIGHT_THEME = 0
        private const val DARK_THEME = 1
        private const val TYPE_THEME = "themeType"
        private const val CURRANT_LANGUAGE_POS = "currentLanguagePos"
        private const val CURRANT_CURRENCY_POS = "currentCurrencyPos"
        private const val CURRANT_CURRENCY_TYPE = "currentCurrencyType"
        private const val DEFAULT_POS = 0
        private const val DEFAULT_TYPE = false
        private const val KEY_LANGUAGE_LIST_FRAGMENT = "key_language_from_fragment"
        private const val KEY_CHOSEN_LANGUAGE_LIST_FRAGMENT = "key_SET_language_from_fragment"
        private const val TAG_DIALOG_LANGUAGE = "Dialog_language"
        private const val REQUEST_KEY_LANGUAGE_ITEM = "RequestKey_LANGUAGE_item"
        private const val KEY_LANGUAGE_ITEMS_DIALOG = "key_language_from_dialog"

        private var typeCurrency = false
        private var currencyPos = 0

    }

    private fun backup() {
        export()

        import()
    }

    private fun import() {
        binding.bImport.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            val dialog =  builder
                .setTitle(getString(R.string.dialog_title_import_db))
                .setMessage(getString(R.string.dialog_message_import_db))
                .setPositiveButton(getString(R.string.button_yes)){
                        dialog, id ->
                    importDatabaseFile.invoke().also { //export DB
                        finishImport()
                    }
                }
                .setNegativeButton(getString(R.string.search_cancel), null)
                .create()
            dialog.show()
        }
    }

    private fun finishImport() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity).apply {
            setNegativeButton(getString(R.string.button_ok), null)
            setMessage(getString(R.string.dialog_message_finish_import))
            create()
            show()
        }
    }

    private fun export() {
        binding.bExport.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            val dialog =  builder
                .setTitle(getString(R.string.dialog_title_export_db))
                .setMessage(getString(R.string.dialog_message_export_db))
                .setPositiveButton(getString(R.string.button_yes)){
                        dialog, id ->
                    exportDatabaseFile.invoke().also { //export DB
                        finishExport()
                    }
                }
                .setNegativeButton(getString(R.string.search_cancel), null)
                .create()
            dialog.show()
        }
    }

    private fun finishExport() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity).apply {
            setNegativeButton(getString(R.string.button_ok), null)
            setMessage(getString(R.string.dialog_message_finish_export))
            create()
            show()
        }
    }


    private fun setDefaultValues(){
        //Get statement of Currency in Share preference
        val sharedPref = requireActivity().getPreferences(MODE_PRIVATE)
        typeCurrency = sharedPref.getBoolean(CURRANT_CURRENCY_TYPE, DEFAULT_TYPE)
        currencyPos = sharedPref.getInt(CURRANT_CURRENCY_POS, Currency.United_States.ordinal)
    }

    private fun spinnerCurrency() {
        val spinnerAdapter = object: ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listCurrency()
        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView
                // set item text size
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP,12F)
                // set spinner item padding
                view.setPadding(
                    5.toDp(context), // left
                    3.toDp(context), // top
                    5.toDp(context), // right
                    3.toDp(context) // bottom
                )
                return view
            }
        }
        binding.spinnerCurrency.adapter = spinnerAdapter
        binding.spinnerCurrency.setSelection(currencyPos) //set Current Pos


        // Set an on item selected listener for spinner object
        binding.spinnerCurrency.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {//parent.getItemAtPosition(position).toString()
                if(binding.rbCode.isChecked){
                    CurrentCurrency.type = false
                    CurrentCurrency.currency = Currency.values().get(position).code
                    setSharePref()
                }else{
                    CurrentCurrency.type = true
                    CurrentCurrency.currency = Currency.values().get(position).symbol
                    setSharePref()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {// Another interface callback}
            }
        }
    }

    private fun listCurrency(): Array<out String> {
        var list = arrayOf<String>()
        Currency.values().forEach {
            list += it.name
        }
        return list
    }

    private fun radioButtonsCurrency(){
        if(typeCurrency)
            binding.rbSymbol.isChecked = true
        else
            binding.rbCode.isChecked = true

        binding.rbCode.setOnClickListener{
                CurrentCurrency.type = false
                CurrentCurrency.currency = Currency.values().get(binding.spinnerCurrency.selectedItemPosition).code
                setSharePref()
        }

        binding.rbSymbol.setOnClickListener {
                CurrentCurrency.type = true
                CurrentCurrency.currency = Currency.values().get(binding.spinnerCurrency.selectedItemPosition).symbol
                setSharePref()
        }
    }

    private fun setSharePref(){
        //Save statement of Currency in Share preference
        val sharedPref = requireActivity().getPreferences(MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(CURRANT_CURRENCY_TYPE, CurrentCurrency.type)
            putInt(CURRANT_CURRENCY_POS, binding.spinnerCurrency.selectedItemPosition)
            apply()
        }
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
            if(Language.values().get(item).Name != binding.bLanguage.text)
                dialogDeleteImage(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dialogDeleteImage(item: Int){
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialog =  builder
            .setTitle(getString(R.string.dialog_title_change_lang))
            .setMessage(getString(R.string.dialog_message_change_lang_dialog))
            .setPositiveButton(getString(R.string.button_yes)){
                    dialog, id ->
                setLocate(item) //Change language
            }
            .setNegativeButton(getString(R.string.search_cancel), null)
            .create()
        dialog.show()
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
        if(position != DEFAULT_POS) {
            val locale = Locale(Language.values().get(position).shortName)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        }
        else{
            val locale = Locale(ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        }
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

    private fun radioButtonsColorState() {
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
        binding.rbSymbol.buttonTintList = buttonStates
        binding.rbCode.buttonTintList = buttonStates
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