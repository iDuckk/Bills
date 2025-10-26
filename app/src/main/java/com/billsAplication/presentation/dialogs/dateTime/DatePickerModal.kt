package com.billsAplication.presentation.dialogs.dateTime

import androidx.compose.foundation.background
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.billsAplication.R
import com.billsAplication.extension.getCurrentDateMls
import com.billsAplication.extension.setFormattingDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    color: Color,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(getCurrentDateMls())

    DatePickerDialog(
        modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
        onDismissRequest = onDismiss, properties = DialogProperties(),
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(setFormattingDate(datePickerState.selectedDateMillis))
                onDismiss()
            }) {
                Text(
                    text = stringResource(R.string.button_ok),
                    color = color
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.search_cancel),
                    color = color
                )
            }
        },
        content = {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
                colors = DatePickerDefaults.colors(
                    titleContentColor = color,
                    weekdayContentColor = color,
                    selectedDayContainerColor = color,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayContentColor = color,
                    todayDateBorderColor = color
                ),
            )
        }
    )
}