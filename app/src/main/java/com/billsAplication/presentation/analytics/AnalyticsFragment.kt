package com.billsAplication.presentation.analytics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAnalyticsBinding
import com.billsAplication.presentation.billsList.BillsListViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import javax.inject.Inject

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding: FragmentAnalyticsBinding get() = _binding!!

    @Inject
    lateinit var viewModel: BillsListViewModel

    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val NEXT_MONTH = true
    private val PREV_MONTH = false

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
    ): View? {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPieChart()

        setDataToPieChart()

        titleBar()



        firstEntrance()

    }

    private fun firstEntrance() {
        //SetColor state
        binding.tvExpense.setBackgroundResource(R.drawable.textview_fullbackground_expense)
        binding.tvIncome.setBackgroundResource(R.drawable.textview_border_income)
        binding.tvExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_income))
        binding.tvExpense.isEnabled = false
        binding.tvIncome.isEnabled = true
        //set centre title of pieChart
        binding.pieChart.centerText = "Expense"
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun titleBar() {
        //Set month`s text in bar
        binding.tvMonth.text = viewModel.currentDate
        binding.tvMonth.setOnClickListener {
            viewModel.currentDate
            binding.tvMonth.text = viewModel.currentDate()
            viewModel.defaultMonth()
            //set a new list
//            setNewList(binding.tvMonth.text.toString())
        }
        //Previous month
        binding.imBackMonth.setOnClickListener {
            viewModel.currentDate = viewModel.changeMonthBar(PREV_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
//            setNewList(binding.tvMonth.text.toString())
        }
        //Next month
        binding.imNextMonth.setOnClickListener {
            viewModel.currentDate = viewModel.changeMonthBar(NEXT_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
//            setNewList(binding.tvMonth.text.toString())
        }

        binding.tvExpense.setOnClickListener{
            //SetColor state
            binding.tvExpense.setBackgroundResource(R.drawable.textview_fullbackground_expense)
            binding.tvIncome.setBackgroundResource(R.drawable.textview_border_income)
            binding.tvExpense.setTextColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
            binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_income))
            binding.tvExpense.isEnabled = false
            binding.tvIncome.isEnabled = true
            //set centre title of pieChart
            binding.pieChart.centerText = "Expense"
            binding.pieChart.invalidate()
        }

        binding.tvIncome.setOnClickListener{
            //SetColor state
            binding.tvExpense.setBackgroundResource(R.drawable.textview_border_expense)
            binding.tvIncome.setBackgroundResource(R.drawable.textview_fullbackground_income)
            binding.tvIncome.setTextColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
            binding.tvExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_expense))
            binding.tvExpense.isEnabled = true
            binding.tvIncome.isEnabled = false
            //set centre title of pieChart
            binding.pieChart.centerText = "Income"
            binding.pieChart.refreshDrawableState()
            binding.pieChart.invalidate()
        }

    }

    private fun initPieChart() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.text = ""
            //hollow pie chart
            isDrawHoleEnabled = false
            setTouchEnabled(false)
            setDrawEntryLabels(false)
            //adding padding
            setExtraOffsets(20f, 0f, 20f, 20f)
            setUsePercentValues(true)
            isRotationEnabled = false
            setDrawEntryLabels(false)
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.isWordWrapEnabled = true
        }
    }

    private fun setDataToPieChart() {
        val dataEntries = ArrayList<PieEntry>()
        dataEntries.add(PieEntry(72f, "Android"))
        dataEntries.add(PieEntry(26f, "Ios"))
        dataEntries.add(PieEntry(2f, "Other"))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#4DD0E1"))
        colors.add(Color.parseColor("#FFF176"))
        colors.add(Color.parseColor("#FF8A65"))

        val dataSet = PieDataSet(dataEntries, "")
        val data = PieData(dataSet)

        // In Percentage
        data.setValueFormatter(PercentFormatter())
        dataSet.sliceSpace = 3f
        dataSet.colors = colors
        binding.pieChart.data = data
        data.setValueTextSize(15f)
        binding.pieChart.setExtraOffsets(5f, 5f, 5f, 5f)
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)

        //create hole in center
        binding.pieChart.holeRadius = 58f
        binding.pieChart.transparentCircleRadius = 61f
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.setHoleColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))

        //add text in center
        binding.pieChart.setDrawCenterText(true)
//        binding.pieChart.centerText = "Mobile OS Market share"



        binding.pieChart.invalidate()
    }

    @ColorInt
    fun Context.getColorFromAttr(@AttrRes attrColor: Int
    ): Int {
        val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
        val textColor = typedArray.getColor(0, 0)
        typedArray.recycle()
        return textColor
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}