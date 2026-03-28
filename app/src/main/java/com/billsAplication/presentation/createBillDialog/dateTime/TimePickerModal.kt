package com.billsAplication.presentation.createBillDialog.dateTime

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.billsAplication.R
import com.billsAplication.extension.getCurrentHour
import com.billsAplication.extension.getCurrentMinutes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.ui.graphics.Color
import com.billsAplication.extension.convertTo12HourFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    color: Color,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = getCurrentHour(), initialMinute = getCurrentMinutes(), is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.search_cancel),
                    color = color
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(
                        convertTo12HourFormat(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(R.string.button_ok),
                    color = color
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.onPrimary,
        text = {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
                colors = TimePickerDefaults.colors(
                    clockDialColor = MaterialTheme.colorScheme.surface,
                    selectorColor = color,
                    periodSelectorBorderColor = color,
                    periodSelectorSelectedContainerColor = color,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    timeSelectorSelectedContentColor = color,
                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    )
}
