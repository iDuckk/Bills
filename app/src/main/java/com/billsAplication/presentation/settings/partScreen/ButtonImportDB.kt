package com.billsAplication.presentation.settings.partScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.billsAplication.R
import com.billsAplication.presentation.settings.dialog.SettingsAlertDialog
import com.billsAplication.presentation.settings.dialog.SettingsAlertDialogInfo
import com.billsAplication.presentation.settings.view.ButtonSettings
@Composable
fun ButtonImportDB(
    color: Color,
    settingsAlertDialog: () -> Unit
) {
    val openAlertDialog = remember { mutableStateOf(false) }
    if (openAlertDialog.value) {
        SettingsAlertDialog(
            onDismissRequest = {
                openAlertDialog.value = false
            },
            onConfirmation = {
                openAlertDialog.value = false
                settingsAlertDialog.invoke()
            },
            dialogTitle = stringResource(R.string.dialog_title_import_db),
            dialogText = stringResource(R.string.dialog_message_import_db),
            color = color
        )
    }
    ButtonSettings(
        text = stringResource(R.string.settings_import_db),
        color = color
    ) {
        openAlertDialog.value = true
    }
}