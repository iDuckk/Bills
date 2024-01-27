package com.billsAplication.presentation.analytics

import PieChart
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAnalyticsBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.analytics.view.BIllsItemAnalytics
import com.billsAplication.presentation.analytics.view.ButtonAnalytics
import com.billsAplication.presentation.analytics.view.ListCategory
import com.billsAplication.presentation.analytics.view.MonthPicker
import com.billsAplication.utils.ColorsPie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding: FragmentAnalyticsBinding get() = _binding!!

    @Inject
    lateinit var viewModel: AnalyticsViewModel

    private val COLORS_AMOUNT = 29
    private val spacerType = 10.dp
    private val spacerTitle = 5.dp

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

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    AnalyticsScreen()
                }
            }
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    @Composable
    private fun AnalyticsScreen() {
        val isClickedExpenses = remember {
            mutableStateOf(true)
        }
        val isClickedIncome = remember {
            mutableStateOf(false)
        }
        val month = remember {
            mutableStateOf(viewModel.currentDate())
        }
        val categoryList: MutableState<List<BillsItem>> = remember {
            mutableStateOf(listOf<BillsItem>())
        }
        TopBar(
            isClickedExpenses = isClickedExpenses,
            isClickedIncome = isClickedIncome,
            month = month
        )
        Spacer(Modifier.size(spacerType))
        ListType(
            isClickedExpenses = isClickedExpenses,
            isClickedIncome = isClickedIncome,
            categoryList = categoryList,
            month = month)
    }

    @Composable
    private fun TopBar(
        isClickedExpenses: MutableState<Boolean>,
        isClickedIncome: MutableState<Boolean>,
        month: MutableState<String>
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = spacerType)
                .fillMaxWidth()
        ) {
            MonthPicker(
                viewModel = viewModel,
                month = month,
                padding = spacerType
            )
            Spacer(Modifier.size(spacerType))
            Row {
                ButtonAnalytics(
                    text = stringResource(id = R.string.bill_list_expense),
                    color = colorResource(id = R.color.text_expense),
                    isClicked = isClickedExpenses
                ) {
                    isClickedExpenses.value = true
                    isClickedIncome.value = false
                }
                Spacer(Modifier.size(spacerType))
                ButtonAnalytics(
                    text = stringResource(id = R.string.bills_list_income),
                    color = colorResource(id = R.color.text_income),
                    isClicked = isClickedIncome
                ) {
                    isClickedIncome.value = true
                    isClickedExpenses.value = false
                }
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    private fun BasicPieChart(
        isClickedExpenses: MutableState<Boolean>,
        isClickedIncome: MutableState<Boolean>,
        categoryList: MutableState<List<BillsItem>>,
        month: MutableState<String>
    ) {
        val coroutineScope = rememberCoroutineScope()

        val chartValuesState: MutableState<List<Float>> = remember {
            mutableStateOf(listOf<Float>(0f))
        }
        val chartColorsState: MutableState<List<Color>> = remember {
            mutableStateOf(listOf<Color>(Color.Transparent))
        }
        val chartCategoryState: MutableState<List<String>> = remember {
            mutableStateOf(listOf<String>(""))
        }

        ListCategory(
            chartColorsState = chartColorsState,
            chartCategoryState = chartCategoryState,
            bigDp = spacerType
        )
//        Color(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorPrimaryVariant))
        PieChart(
            modifier = Modifier.padding(30.dp),
            colors = chartColorsState.value,
            inputValues = chartValuesState.value,
            textColor = Color.DarkGray
        ) { indexCategory ->
            coroutineScope.launch(Dispatchers.IO) {
                categoryList.value = viewModel.getMonthListByTypeCategory(
                    month = month.value,
                    type = if (isClickedExpenses.value) BillsItem.TYPE_EXPENSES else BillsItem.TYPE_INCOME,
                    category = chartCategoryState.value[indexCategory]
                )
            }
        }

        if (isClickedExpenses.value) {
            SetEntriesPieChart(
                month = month.value,
                type = BillsItem.TYPE_EXPENSES,
                coroutineScope = coroutineScope,
                chartValuesState = chartValuesState,
                chartColorsState = chartColorsState,
                chartCategoryState = chartCategoryState,
            )
        }
        if (isClickedIncome.value) {
            SetEntriesPieChart(
                month = month.value,
                type = BillsItem.TYPE_INCOME,
                coroutineScope = coroutineScope,
                chartValuesState = chartValuesState,
                chartColorsState = chartColorsState,
                chartCategoryState = chartCategoryState,
            )
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    private fun SetEntriesPieChart(
        month: String,
        type: Int,
        coroutineScope: CoroutineScope,
        chartValuesState: MutableState<List<Float>>,
        chartColorsState: MutableState<List<Color>>,
        chartCategoryState: MutableState<List<String>>
    ) {
        var sum = 0f

        val chartValues = mutableListOf<Float>()
        val chartColors = mutableListOf<Color>()
        val chartCategory = mutableListOf<String>()

        coroutineScope.launch(Dispatchers.Main) {
            sum = async(Dispatchers.IO) {
                viewModel.summaryAmount(
                    month = month,
                    type = type
                ).toFloat()
            }.await()

            launch(Dispatchers.IO) {
                viewModel.getMonthListByType(
                    month = month,
                    type = type
                ).let { list ->
                    list.sortedBy { it.category }.forEachIndexed { index, item ->

                        val valueFloat = if (item.amount.toFloat() == 0f)
                            0f
                        else
                            item.amount.toFloat() / sum

                        val colorInt = if (index < COLORS_AMOUNT) index else index - COLORS_AMOUNT
                        val colorStr = ColorsPie.entries[colorInt].printableName
                        val color = Color(android.graphics.Color.parseColor(colorStr))

                        if (chartCategory.contains(item.category)) {   //if category item exists
                            chartValues.last() + valueFloat
                        } else {
                            //if new category item
                            chartValues.add(valueFloat)
                            chartColors.add(color)
                            chartCategory.add(item.category)
                        }

                    }

                    if (list.isEmpty()) {
                        chartValues.add(0f)
                        chartColors.add(Color.Transparent)
                        chartCategory.add("")
                    }

                }
            }.join()

            chartCategoryState.value = chartCategory
            chartColorsState.value = chartColors
            chartValuesState.value = chartValues
        }
    }

    @Composable
    private fun ListType(
        isClickedExpenses: MutableState<Boolean>,
        isClickedIncome: MutableState<Boolean>,
        categoryList: MutableState<List<BillsItem>>,
        month: MutableState<String>
    ) {
        LazyColumn(content = {
            item {
                BasicPieChart(
                    isClickedExpenses = isClickedExpenses,
                    isClickedIncome = isClickedIncome,
                    categoryList = categoryList,
                    month = month
                )
            }
            items(items = categoryList.value, itemContent = { item ->
                BIllsItemAnalytics(
                    date = item.date,
                    category = item.category,
                    note = item.note,
                    amount = item.amount,
                    type = item.type,
                    smallDp = spacerTitle,
                    bigDp = spacerType
                )
            }, key = { it.id })
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}