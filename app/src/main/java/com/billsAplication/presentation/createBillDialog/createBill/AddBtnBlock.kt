package com.billsAplication.presentation.createBillDialog.createBill

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.extension.convertDateToMonthYear
import com.billsAplication.extension.convertTo24HourFormat
import com.billsAplication.utils.PagingConstants.DP_50
import com.billsAplication.utils.StateColorButton.Companion.getColorType

@Composable
fun AddBtnBlock(
    bill: BillsItem?,
    type: MutableState<Int>,
    date: MutableState<String>,
    time: MutableState<String>,
    category: MutableState<String>,
    amount: MutableState<TextFieldValue>,
    note: MutableState<String>,
    description: MutableState<String>,
    images: List<String>,
    onClick: (BillsItem) -> Unit,
) {
    val context = LocalContext.current
    Button(
        onClick = {
            val newItem = BillsItem(
                id = bill?.id ?: 0,
                type = type.value,
                month = convertDateToMonthYear(date.value),
                date = date.value,
                time = convertTo24HourFormat(time.value),
                category = category.value,
                amount = amount.value.text.ifEmpty { "0.0" },
                note = note.value,
                description = description.value,
                bookmark = false,
                image1 = images.getOrNull(0) ?: "",
                image2 = images.getOrNull(1) ?: "",
                image3 = images.getOrNull(2) ?: "",
                image4 = images.getOrNull(3) ?: "",
                image5 = images.getOrNull(4) ?: ""
            )
            onClick.invoke(newItem)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = getColorType(context = context, type = type.value),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = DP_50)
    ) {
        Text(text = stringResource(R.string.title_add))
    }
}