package com.billsAplication.presentation.createBillDialog.createBill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.billsAplication.R
import com.billsAplication.extension.localCurrentDate
import com.billsAplication.extension.localCurrentTime
import com.billsAplication.presentation.components.InputText
import com.billsAplication.presentation.createBillDialog.dateTime.DatePickerModal
import com.billsAplication.presentation.createBillDialog.dateTime.TimePickerModal
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.StateColorButton.Companion.getColorType

@Composable
fun DateTimeBlock(
    date: MutableState<String>,
    time: MutableState<String>,
    isEditable: Boolean,
    type: Int,
    showDatePicker: MutableState<Boolean>,
    showTimePicker: MutableState<Boolean>,
    showCategoryPicker: MutableState<Boolean>,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    if (showDatePicker.value) {
        DatePickerModal(
            color = getColorType(context = context, type = type),
            onDateSelected = { selectedDate ->
                date.value = selectedDate
                focusManager.clearFocus()
            },
            onDismiss = {
                showDatePicker.value = false
                focusManager.clearFocus()
            }
        )
    }

    if (showTimePicker.value) {
        TimePickerModal(
            color = getColorType(context = context, type = type),
            onTimeSelected = { selectedTime ->
                time.value = selectedTime
                focusManager.clearFocus()
            },
            onDismiss = {
                showDatePicker.value = false
                focusManager.clearFocus()
            }
        )
    }

    Row(
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        if (date.value == "") date.value = localCurrentDate()
        if (time.value == "") time.value = localCurrentTime()


        InputText(
            label = stringResource(R.string.title_date),
            value = date.value,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            isEditable = isEditable,
            readOnly = true,
            type = type,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged {
                    if (it.isFocused) showCategoryPicker.value = false
                    showDatePicker.value = it.isFocused
                },
            onValueChange = { date.value = it },
            onDone = {}
        )

        Spacer(Modifier.width(DP_10))

        InputText(
            label = stringResource(R.string.title_time),
            value = time.value,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            isEditable = isEditable,
            readOnly = true,
            type = type,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged {
                    if (it.isFocused) showCategoryPicker.value = false
                    showTimePicker.value = it.isFocused
                },
            onValueChange = { time.value = it },
            onDone = {}
        )
    }
}
