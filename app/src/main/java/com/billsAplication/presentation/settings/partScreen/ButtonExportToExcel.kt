package com.billsAplication.presentation.settings.partScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.presentation.settings.dialog.SettingsAlertDialog
import com.billsAplication.presentation.settings.dialog.SettingsAlertDialogInfo
import com.billsAplication.presentation.settings.view.ButtonSettings
import com.billsAplication.utils.getColorFromAttr

@Composable
fun ButtonExportToExcel(
    openAlertDialogInfo: MutableState<Boolean>,
    padding: Dp,
    colorButton: Color,
    settingsAlertDialog: () -> Unit,
    settingsAlertDialogInfo: () -> Unit
    ) {
    val context = LocalContext.current
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
            dialogTitle = stringResource(R.string.dialog_title_export_db_excel),
            dialogText = stringResource(R.string.dialog_message_export_db_excel)
        )
    }

    if (openAlertDialogInfo.value) {
        SettingsAlertDialogInfo(text = stringResource(R.string.dialog_message_finish_export_excel)) {
            openAlertDialogInfo.value = false
            settingsAlertDialogInfo.invoke()
        }
    }

    Text(
        color = Color(context.getColorFromAttr(com.google.android.material.R.attr.colorPrimaryVariant)),
        fontSize = 12.sp,
        text = stringResource(R.string.title_excel)
    )
    Spacer(androidx.compose.ui.Modifier.size(padding))
    ButtonSettings(
        text = stringResource(R.string.bSettings_ExportToExcel),
        color = colorButton
    ) {
        openAlertDialog.value = true
    }

}