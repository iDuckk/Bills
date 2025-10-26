package com.billsAplication.presentation.dialogs.createBill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_EXPENSES
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_INCOME
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_50

@Composable
fun TypeBtnBlock(
    type: MutableState<Int>,
    showMainBlock: MutableState<Boolean>,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Column {
        Button(
            onClick = {
                showMainBlock.value = true
                type.value = TYPE_EXPENSES
                onClick.invoke()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(context.getColor(R.color.text_expense)),
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.bill_list_expense))
        }

        Spacer(Modifier.height(DP_10))

        Button(
            onClick = {
                showMainBlock.value = true
                type.value = TYPE_INCOME
                onClick.invoke()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(context.getColor(R.color.text_income)),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = DP_50)
        ) {
            Text(text = stringResource(R.string.bills_list_income))
        }
    }
}
