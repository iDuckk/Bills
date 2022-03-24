package com.billsAplication.presentation.addBill

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAddBillBinding
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.editTextDateAdd.setOnClickListener {
            //TODO
        }

        binding.editTextTimeAdd.setOnClickListener {
            //TODO
        }

        binding.edAddCategory.setOnClickListener {
            //TODO
        }

        binding.editTextDateAdd.setOnFocusChangeListener { view, b ->
            setColorStateEditText(DATE, colorState)
        }

        binding.editTextTimeAdd.setOnFocusChangeListener { view, b ->
            setColorStateEditText(TIME, colorState)
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
            //TODO IF ADD
        }else{
            //TODO IF EDIT
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isFocusEditText(colorState : ColorStateList){
        if(binding.editTextDateAdd.isFocused) binding.editTextDateAdd.backgroundTintList = colorState
        if(binding.editTextTimeAdd.isFocused) binding.editTextTimeAdd.backgroundTintList = colorState
        if(binding.edAddCategory.isFocused) binding.edAddCategory.backgroundTintList = colorState
        if(binding.edAddAmount.isFocused) binding.edAddAmount.backgroundTintList = colorState
        if(binding.edAddNote.isFocused) binding.edAddNote.backgroundTintList = colorState
        if(binding.edDescription.isFocused) binding.edDescription.backgroundTintList = colorState
    }

    private fun setColorStateEditText(editText : Int, colorState : ColorStateList){
        binding.editTextDateAdd.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_background))
        binding.editTextTimeAdd.backgroundTintList =
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
            DATE -> binding.editTextDateAdd.backgroundTintList = colorState
            TIME -> binding.editTextTimeAdd.backgroundTintList = colorState
            CATEGORY -> binding.edAddCategory.backgroundTintList = colorState
            AMOUNT -> binding.edAddAmount.backgroundTintList = colorState
            NOTE -> binding.edAddNote.backgroundTintList = colorState
            DESCRIPTION -> binding.edDescription.backgroundTintList = colorState
        }
    }

}