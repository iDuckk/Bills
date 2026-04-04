package com.billsAplication.presentation.createBillDialog.createBill

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    cancelButtonText: String,
    showDialog: MutableState<Boolean>,
    onDeleteConfirmed: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(title)
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    onDeleteConfirmed()
                }) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog.value = false
                }) {
                    Text(cancelButtonText)
                }
            }
        )
    }
}
