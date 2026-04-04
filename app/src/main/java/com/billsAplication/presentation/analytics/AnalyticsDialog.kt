package com.billsAplication.presentation.analytics

import PieChart
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true   // чтобы не было "половинного" состояния
    )

    val isClickedExpenses = remember { mutableStateOf(true) }
    val isClickedIncome = remember { mutableStateOf(false) }

    if (showDialog.value) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = {
                showDialog.value = false
                onDismiss()
            }
        ) {
            // Главный скроллящийся контент без жёсткого ограничения высоты
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DP_15)
                    .verticalScroll(rememberScrollState())
                    .imePadding() // если будет клавиатура — полезно
            ) {
                Text(
                    text = stringResource(R.string.menu_analytics),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(DP_10))

                // Кнопки Expense / Income
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ButtonAnalytics(
                        text = stringResource(R.string.bill_list_expense),
                        color = colorResource(R.color.text_expense),
                        isSelected = isClickedExpenses.value
                    ) {
                        isClickedExpenses.value = true
                        isClickedIncome.value = false
                    }
                    Spacer(Modifier.width(DP_10))
                    ButtonAnalytics(
                        text = stringResource(R.string.bills_list_income),
                        color = colorResource(R.color.text_income),
                        isSelected = isClickedIncome.value
                    ) {
                        isClickedIncome.value = true
                        isClickedExpenses.value = false
                    }
                }

                Spacer(Modifier.height(DP_10))

                AnalyticsPieContent(
                    isExpensesType = isClickedExpenses.value,
                    bills = bills
                )

                Spacer(modifier = Modifier.height(DP_15))
            }
        }
    }
}

@Composable
private fun AnalyticsPieContent(
    isExpensesType: Boolean,
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
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListCategory(
            chartColorsState = chartColorsState,
            chartCategoryState = chartCategoryState,
            bigDp = DP_10
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ← Вот здесь главное изменение
        PieChart(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .aspectRatio(1f)           // квадрат остаётся
                .heightIn(max = 320.dp),   // ← ограничиваем максимальную высоту чарта
            type = if (isExpensesType) stringResource(R.string.bill_list_expense)
            else stringResource(R.string.bills_list_income),
            colors = chartColorsState.value,
            inputValues = chartValuesState.value,
            textColor = Color.DarkGray
        ) {
            // No action needed
        }
    }
}
