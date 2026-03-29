package com.billsAplication.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_5

@Composable
fun AmountBar(
    incAmountState: MutableState<String>,
    expAmountState: MutableState<String>,
    totalAmountState: MutableState<String>,
    isSortDescending: MutableState<Boolean>,
    filterState: MutableIntState
) {
    val filterFullList = 3 // Custom value for full list

    Row(
        horizontalArrangement = Arrangement.Absolute.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(DP_10)
            .fillMaxWidth()
    ) {
        // Income Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(bottom = DP_5)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (filterState.intValue == BillsItem.TYPE_INCOME) {
                            isSortDescending.value = !isSortDescending.value
                        } else {
                            filterState.intValue = BillsItem.TYPE_INCOME
                        }
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.bills_list_income),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                if (filterState.intValue == BillsItem.TYPE_INCOME) {
                    SortIcon(isSortDescending.value)
                }
            }
            Text(
                text = incAmountState.value,
                color = colorResource(id = R.color.text_income),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        // Expense Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(bottom = DP_5)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (filterState.intValue == BillsItem.TYPE_EXPENSES) {
                            isSortDescending.value = !isSortDescending.value
                        } else {
                            filterState.intValue = BillsItem.TYPE_EXPENSES
                        }
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.bill_list_expense),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                if (filterState.intValue == BillsItem.TYPE_EXPENSES) {
                    SortIcon(isSortDescending.value)
                }
            }
            Text(
                text = expAmountState.value,
                color = colorResource(id = R.color.text_expense),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        // Total Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(bottom = DP_5)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (filterState.intValue == filterFullList) {
                            isSortDescending.value = !isSortDescending.value
                        } else {
                            filterState.intValue = filterFullList
                        }
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.bill_list_total),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                if (filterState.intValue == filterFullList) {
                    SortIcon(isSortDescending.value)
                }
            }
            Text(
                text = totalAmountState.value,
                color = Color.Gray,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SortIcon(isDescending: Boolean) {
    Icon(
        painter = painterResource(
            id = if (isDescending) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up
        ),
        contentDescription = "Sort",
        tint = Color.Gray,
        modifier = Modifier
            .size(20.dp)
            .padding(start = 2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AmountBarPreview() {
    val incAmountState = remember { mutableStateOf("0.0") }
    val expAmountState = remember { mutableStateOf("0.0") }
    val totalAmountState = remember { mutableStateOf("0.0") }
    val isSortDescending = remember { mutableStateOf(true) }
    val filterState = remember { mutableIntStateOf(3) }
    AmountBar(incAmountState, expAmountState, totalAmountState, isSortDescending, filterState)
}