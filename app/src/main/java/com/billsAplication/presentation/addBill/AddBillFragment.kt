package com.billsAplication.presentation.addBill

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAddBillBinding
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class AddBillFragment : Fragment() {

    private var _binding: FragmentAddBillBinding? = null
    private val binding : FragmentAddBillBinding get() = _binding!!

    private val ADD_BILL_KEY = "add_bill_key"
    private val TYPE_EXPENSE = 0
    private val TYPE_INCOME = 1
    private val DATE = 0
    private val TIME = 1
    private val CATEGORY = 2
    private val AMOUNT = 3
    private val NOTE = 4
    private val DESCRIPTION = 5

    private var TYPE_BILL = TYPE_EXPENSE

    private var bookmark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var checkDatePicker = true
        var checkTimePicker = true

        var colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_expense))

        val bottomNavigation = (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.visibility = View.GONE

        binding.imAddBillBack.setOnClickListener {
            bottomNavigation.visibility = View.VISIBLE
            findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
        }

        binding.imAddBillBookmark.setOnClickListener {
            if(bookmark) {
                binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_disable)
                bookmark = false
            } else {
                binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_enable)
                bookmark = true
            }
            //TODO Bookmark
        }

        binding.tvAddExpenses.setOnClickListener {
            binding.tvAddExpenses.setBackgroundResource(R.drawable.textview_border_expense)
            binding.tvAddIncome.setBackgroundResource(0)
            TYPE_BILL = TYPE_EXPENSE
            colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_expense))
            isFocusEditText(colorState)
        }

        binding.tvAddIncome.setOnClickListener {
            binding.tvAddIncome.setBackgroundResource(R.drawable.textview_border_income)
            binding.tvAddExpenses.setBackgroundResource(0)
            TYPE_BILL = TYPE_INCOME
            colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_income))
            isFocusEditText(colorState)
        }

        binding.bAddSave.setOnClickListener {
            //TODO
            findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
        }

        binding.imFirstPhoto.setOnClickListener {
            //TODO
        }

        binding.imSecondPhoto.setOnClickListener {
            //TODO
        }

        binding.imThirdPhoto.setOnClickListener {
            //TODO
        }

        binding.edAddCategory.setOnClickListener {
            //TODO
        }

        binding.edDateAdd.setOnFocusChangeListener { view, b ->
            setColorStateEditText(DATE, colorState)
            //Picker double calls. Because of setText calls ClickListener
            if(checkDatePicker) {
                initDatePickerDialog()
                checkDatePicker = false
            }
            binding.edDateAdd.clearFocus()
            checkDatePicker = true
        }

        binding.edTimeAdd.setOnFocusChangeListener { view, b ->
            setColorStateEditText(TIME, colorState)
            //Picker double calls. Because of setText calls ClickListener
            if(checkTimePicker) {
                initTimePicker()
                checkTimePicker = false
            }
            binding.edTimeAdd.clearFocus()
            checkTimePicker = true
        }

        binding.edAddCategory.setOnFocusChangeListener { view, b ->
            setColorStateEditText(CATEGORY, colorState)
        }

        binding.edAddAmount.setOnFocusChangeListener { view, b ->
            setColorStateEditText(AMOUNT, colorState)
        }

        binding.edAddNote.setOnFocusChangeListener { view, b ->
            setColorStateEditText(NOTE, colorState)
        }

        binding.edDescription.setOnFocusChangeListener { view, b ->
            setColorStateEditText(DESCRIPTION, colorState)
        }

        val type = arguments?.getBoolean(ADD_BILL_KEY)
        if(type!!){
            //Set Date
            binding.edDateAdd.setText(SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().time))
            //Set Time
            binding.edTimeAdd.setText(SimpleDateFormat("HH:mm a").format(Calendar.getInstance().time))
        }else{
            //TODO IF EDIT
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isFocusEditText(colorState : ColorStateList){
        if(binding.edDateAdd.isFocused) binding.edDateAdd.backgroundTintList = colorState
        if(binding.edTimeAdd.isFocused) binding.edTimeAdd.backgroundTintList = colorState
        if(binding.edAddCategory.isFocused) binding.edAddCategory.backgroundTintList = colorState
        if(binding.edAddAmount.isFocused) binding.edAddAmount.backgroundTintList = colorState
        if(binding.edAddNote.isFocused) binding.edAddNote.backgroundTintList = colorState
        if(binding.edDescription.isFocused) binding.edDescription.backgroundTintList = colorState
    }

    private fun setColorStateEditText(editText : Int, colorState : ColorStateList){
        binding.edDateAdd.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_background))
        binding.edTimeAdd.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_background))
        binding.edAddCategory.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_background))
        binding.edAddAmount.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_background))
        binding.edAddNote.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_background))
        binding.edDescription.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_background))
        when(editText){
            DATE -> binding.edDateAdd.backgroundTintList = colorState
            TIME -> binding.edTimeAdd.backgroundTintList = colorState
            CATEGORY -> binding.edAddCategory.backgroundTintList = colorState
            AMOUNT -> binding.edAddAmount.backgroundTintList = colorState
            NOTE -> binding.edAddNote.backgroundTintList = colorState
            DESCRIPTION -> binding.edDescription.backgroundTintList = colorState
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initDatePickerDialog(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(requireActivity(), { view, year, monthOfYear, dayOfMonth ->
            c.set(year, monthOfYear, dayOfMonth)
            binding.edDateAdd.setText(SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().time))
            binding.edAddCategory.requestFocus()
        }, year, month, day)

        dpd.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initTimePicker(){
        val c = Calendar.getInstance()
        val cHour = c.get(Calendar.HOUR_OF_DAY)
        val cMinute = c.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(requireContext(),
            { view, hour, minute ->
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                binding.edTimeAdd.setText(SimpleDateFormat("HH:mm a").format(c.time))
                binding.edAddCategory.requestFocus()
            }, cHour, cMinute, false)

        mTimePicker.show()
    }

}