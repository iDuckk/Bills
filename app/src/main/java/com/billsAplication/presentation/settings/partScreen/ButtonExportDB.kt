package com.billsAplication.presentation.settings.partScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.billsAplication.R
import com.billsAplication.presentation.settings.dialog.SettingsAlertDialog
import com.billsAplication.presentation.settings.dialog.SettingsAlertDialogInfo
import com.billsAplication.presentation.settings.view.ButtonSettings
@Composable
fun ButtonExportDB(
    openAlertDialogInfo: MutableState<Boolean>,
    color: Color,
    settingsAlertDialog: () -> Unit,
    settingsAlertDialogInfo: () -> Unit
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
            dialogTitle = stringResource(R.string.dialog_title_export_db),
            dialogText = stringResource(R.string.dialog_message_export_db),
            color = color
        )
    }
    if (openAlertDialogInfo.value) {
        SettingsAlertDialogInfo(text = stringResource(R.string.dialog_message_finish_export)) {
            openAlertDialogInfo.value = false
            settingsAlertDialogInfo.invoke()
        }
    }
    ButtonSettings(
        text = stringResource(R.string.settings_export_db),
        color = color
    ) {
        openAlertDialog.value = true
    }
}