package com.billsAplication.presentation.analytics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAnalyticsBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.bills.BillsAdapter
import com.billsAplication.utils.ColorsPie
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding: FragmentAnalyticsBinding get() = _binding!!

    @Inject
    lateinit var viewModel: AnalyticsViewModel
    @Inject
    lateinit var billAdapter: BillsAdapter

    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val NEXT_MONTH = true
    private val PREV_MONTH = false
    private val EMPTY_STRING = ""
    private val COLORS_AMOUNT = 29

    val wholeList = ArrayList<BillsItem>()
    private val listSet = mutableMapOf<String, BigDecimal>()

    var wholeAmountPie = BigDecimal(0)

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
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPieChart()

        titleBar()

        initRecView()

        firstEntrance()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun firstEntrance() {
        //SetColor state
        binding.tvExpense.setBackgroundResource(R.drawable.textview_fullbackground_expense)
        binding.tvIncome.setBackgroundResource(R.drawable.textview_border_income)
        binding.tvExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_income))
        binding.pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.text_expense))
        binding.tvExpense.isEnabled = false
        binding.tvIncome.isEnabled = true
        //set centre title of pieChart
        binding.pieChart.centerText = getString(R.string.bill_list_expense)
        //set a new list
        setListMonth()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecView() {
        with(binding.recViewAnalytics) {
            layoutManager = LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
            adapter = billAdapter
            binding.recViewAnalytics.itemAnimator = null
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getList(type: Int) {
        viewModel.getMonth(binding.tvMonthAnalytics.text.toString())
        //if list has old values
        listSet.clear()
        wholeList.clear()
        viewModel.list.observe(viewLifecycleOwner) { item ->
            wholeList.addAll(item)
            //create Lists
            viewModel.list.value?.forEach {
                //Get list of Category
                if(it.type == type && it.category.isNotEmpty()) {
                    //total amount
                    wholeAmountPie += BigDecimal(it.amount.replace(",", ""))

                    if(listSet.containsKey(it.category)) {   //if category item exists
                        var amount = listSet.getValue(it.category) //Sum amount for percentage
                        amount += BigDecimal(it.amount.replace(",", ""))
                        listSet.replace(it.category, amount)
                    }else   //if new category item
                        listSet[it.category] = BigDecimal(it.amount.replace(",", ""))
                }
            }
            //Set data for Pie
        setDataToPieChart()
        }
        viewModel.list.removeObservers(this)
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun titleBar() {
        //Set month`s text in bar
        binding.tvMonthAnalytics.text = viewModel.currentDate
        binding.tvMonthAnalytics.setOnClickListener {
            viewModel.currentDate
            binding.tvMonthAnalytics.text = viewModel.currentDate()
            viewModel.defaultMonth()
            //set a new list
            setListMonth()
        }
        //Previous month
        binding.imBackMonthAnalytics.setOnClickListener {
            viewModel.currentDate = viewModel.changeMonthBar(PREV_MONTH)
            binding.tvMonthAnalytics.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
            setListMonth()
        }
        //Next month
        binding.imNextMonthAnalytics.setOnClickListener {
            viewModel.currentDate = viewModel.changeMonthBar(NEXT_MONTH)
            binding.tvMonthAnalytics.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
            setListMonth()
        }

        binding.tvExpense.setOnClickListener{
            //SetColor state
            binding.tvExpense.setBackgroundResource(R.drawable.textview_fullbackground_expense)
            binding.tvIncome.setBackgroundResource(R.drawable.textview_border_income)
            binding.tvExpense.setTextColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
            binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_income))
            binding.pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.text_expense))
            binding.tvExpense.isEnabled = false
            binding.tvIncome.isEnabled = true
            //set centre title of pieChart
            binding.pieChart.centerText = getString(R.string.bill_list_expense)
            setListMonth()
        }

        binding.tvIncome.setOnClickListener{
            //SetColor state
            binding.tvExpense.setBackgroundResource(R.drawable.textview_border_expense)
            binding.tvIncome.setBackgroundResource(R.drawable.textview_fullbackground_income)
            binding.tvIncome.setTextColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
            binding.tvExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_expense))
            binding.pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.text_income))
            binding.tvExpense.isEnabled = true
            binding.tvIncome.isEnabled = false
            //set centre title of pieChart
            binding.pieChart.centerText = getString(R.string.bills_list_income)
            setListMonth()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListMonth() {
        billAdapter.submitList(null)
        if (!binding.tvExpense.isEnabled)
            getList(TYPE_EXPENSES)
        else
            getList(TYPE_INCOME)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initPieChart() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.text = EMPTY_STRING
            //hollow pie chart
            isDrawHoleEnabled = false
            setTouchEnabled(true)
            setDrawEntryLabels(false)
            //adding padding
            setExtraOffsets(20f, 0f, 20f, 20f)
            isRotationEnabled = false
            //Legend
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.isWordWrapEnabled = true
            legend.textSize = 12f
            legend.form = Legend.LegendForm.CIRCLE
            legend.formSize = 12f
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

            setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    //h.x - index, h.y - value, (e as PieEntry).label
                    //Create list categories
                    val list = ArrayList<BillsItem>()
                    wholeList.forEach {
                        if(it.category == (e as PieEntry).label)
                            list.add(it)
                    }

//                    Log.d("TAG", (e as PieEntry).label)
                    billAdapter.submitList(list.sortedByDescending { item -> sortingListValue(item.date + item.time) }.toList())
                }

                override fun onNothingSelected() {}
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDataToPieChart() {
        val dataEntries = ArrayList<PieEntry>()
        val colors: ArrayList<Int> = ArrayList()
        //Set data for Pie
        listSet.onEachIndexed { index, item ->
            //Add percentage and Legend
            dataEntries.add(PieEntry(item.value.toFloat(), item.key))
            //Add colors COLORS_AMOUNT - Because we have only 30 color
            val color = if(index < COLORS_AMOUNT) index else index - COLORS_AMOUNT
            colors.add(Color.parseColor(ColorsPie.values().get(color).printableName))
        }

        val dataSet = PieDataSet(dataEntries, "")
        val data = PieData(dataSet)

        // In Percentage
        data.setValueFormatter(PercentFormatter(binding.pieChart))//sent pieChar is important
        dataSet.sliceSpace = 3f
        dataSet.colors = colors
        binding.pieChart.data = data
        data.setValueTextSize(11f)
        binding.pieChart.setExtraOffsets(5f, 5f, 5f, 5f)
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)

        //create hole in center
        binding.pieChart.holeRadius = 30f
        binding.pieChart.transparentCircleRadius = 40f
        binding.pieChart.isDrawHoleEnabled = true
//        binding.pieChart.setHoleColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
        //add text in center
        binding.pieChart.setDrawCenterText(true)
        //it doesn't have values
        if(listSet.isNullOrEmpty()){
            binding.pieChart.centerText = getString(R.string.no_values_analytics)
            binding.pieChart.setCenterTextColor(requireContext().getColor(R.color.default_background))
            binding.pieChart.setHoleColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))

        }else{
            binding.pieChart.setCenterTextColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
        }

        binding.pieChart.invalidate()
    }

    private fun sortingListValue(date: String): Long{
        if(date != EMPTY_STRING) {  // 27/04/202211:59 AM
            val day = date.dropLast(16)
            val month = date.drop(3).dropLast(13)
            val year = date.drop(6).dropLast(8)
            val hour = date.drop(10).dropLast(6)
            val minute = date.drop(13).dropLast(3)

            val c = Calendar.getInstance()
            c.set(year.toInt(),month.toInt(),day.toInt(), hour.toInt(), minute.toInt())

            return c.timeInMillis
        }
        return 0
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