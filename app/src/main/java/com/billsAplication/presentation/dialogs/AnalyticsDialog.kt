package com.billsAplication.presentation.dialogs

import PieChart
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.analytics.view.BIllsItemAnalytics
import com.billsAplication.presentation.analytics.view.ButtonAnalytics
import com.billsAplication.presentation.analytics.view.ListCategory
import com.billsAplication.utils.ColorsPie
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_15
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDialog(
    showDialog: MutableState<Boolean>,
    bills: List<BillsItem>,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isClickedExpenses = remember { mutableStateOf(true) }
    val isClickedIncome = remember { mutableStateOf(false) }
    
    val categoryDetails: MutableState<List<BillsItem>> = remember { mutableStateOf(listOf()) }

    if (showDialog.value) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = {
                showDialog.value = false
                onDismiss()
            },
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = DP_15)
                    .windowInsetsPadding(WindowInsets.ime)
            ) {
                Text(
                    text = stringResource(R.string.menu_analytics),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(DP_10))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ButtonAnalytics(
                        text = stringResource(id = R.string.bill_list_expense),
                        color = colorResource(id = R.color.text_expense),
                        isSelected = isClickedExpenses.value
                    ) {
                        isClickedExpenses.value = true
                        isClickedIncome.value = false
                    }
                    Spacer(Modifier.size(DP_10))
                    ButtonAnalytics(
                        text = stringResource(id = R.string.bills_list_income),
                        color = colorResource(id = R.color.text_income),
                        isSelected = isClickedIncome.value
                    ) {
                        isClickedIncome.value = true
                        isClickedExpenses.value = false
                    }
                }

                Spacer(Modifier.size(DP_10))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        AnalyticsPieContent(
                            isExpensesType = isClickedExpenses.value,
                            categoryDetails = categoryDetails,
                            bills = bills
                        )
                    }
                    items(items = categoryDetails.value, key = { it.id }) { item ->
                        BIllsItemAnalytics(
                            item = item,
                            smallDp = 5.dp,
                            bigDp = DP_10
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsPieContent(
    isExpensesType: Boolean,
    categoryDetails: MutableState<List<BillsItem>>,
    bills: List<BillsItem>
) {
    val chartValuesState = remember { mutableStateOf(listOf(0f)) }
    val chartColorsState = remember { mutableStateOf(listOf(Color.Transparent)) }
    val chartCategoryState = remember { mutableStateOf(listOf("")) }

    val type = if (isExpensesType) BillsItem.TYPE_EXPENSES else BillsItem.TYPE_INCOME
    val filteredBills = remember(bills, type) { bills.filter { it.type == type } }

    LaunchedEffect(filteredBills) {
        val chartValues = mutableListOf<Float>()
        val chartColors = mutableListOf<Color>()
        val chartCategory = mutableListOf<String>()
        
        val sum = filteredBills.sumOf { it.amount.replace(",", "").toDouble() }.toFloat()

        if (filteredBills.isEmpty()) {
            chartValuesState.value = listOf(0f)
            chartColorsState.value = listOf(Color.Transparent)
            chartCategoryState.value = listOf("")
        } else {
            val groupedBills = filteredBills.groupBy { it.category }
            groupedBills.keys.sorted().forEachIndexed { index, category ->
                val categorySum = groupedBills[category]?.sumOf { it.amount.replace(",", "").toDouble() }?.toFloat() ?: 0f
                val valueFloat = if (sum == 0f) 0f else categorySum / sum
                
                val colorInt = if (index < 29) index else index % 29
                val colorStr = ColorsPie.entries[colorInt].printableName
                val color = Color(colorStr.toColorInt())

                chartValues.add(valueFloat)
                chartColors.add(color)
                chartCategory.add(category)
            }
            chartValuesState.value = chartValues
            chartColorsState.value = chartColors
            chartCategoryState.value = chartCategory
        }
        categoryDetails.value = emptyList() 
    }

    Column {
        ListCategory(
            chartColorsState = chartColorsState,
            chartCategoryState = chartCategoryState,
            bigDp = DP_10
        )

        PieChart(
            modifier = Modifier.padding(20.dp),
            type = if (isExpensesType) stringResource(id = R.string.bill_list_expense) else stringResource(id = R.string.bills_list_income),
            colors = chartColorsState.value,
            inputValues = chartValuesState.value,
            textColor = Color.DarkGray
        ) { indexCategory ->
            if (indexCategory >= 0 && indexCategory < chartCategoryState.value.size) {
                val selectedCategory = chartCategoryState.value[indexCategory]
                categoryDetails.value = filteredBills.filter { it.category == selectedCategory }
            }
        }
    }
}
