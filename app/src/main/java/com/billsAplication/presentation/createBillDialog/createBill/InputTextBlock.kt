package com.billsAplication.presentation.createBillDialog.createBill

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.billsAplication.R
import com.billsAplication.presentation.components.InputText

@Composable
fun InputTextBlock(
    amount: MutableState<TextFieldValue>,
    note: MutableState<String>,
    description: MutableState<String>,
    isEditable: MutableState<Boolean>,
    type: MutableState<Int>,
    showCategoryPicker: MutableState<Boolean>,
) {
    // Amount
    CurrencyInputField(
        value = amount.value,
        type = type,
        isEditable = isEditable,
        onValueChange = { amount.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) showCategoryPicker.value = false
            }
    )

    // Note
    InputText(
        label = stringResource(R.string.title_note),
        value = note,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ),
        isEditable = isEditable,
        type = type,
        modifier = Modifier
            .onFocusChanged {
                if (it.isFocused) showCategoryPicker.value = false
            }
            .fillMaxWidth(),
        onValueChange = { newText ->
            note.value = newText.take(20)
        },
        onDone = {}
    )

    // Description
    InputText(
        label = stringResource(R.string.title_description),
        value = description,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ),
        isEditable = isEditable,
        type = type,
        modifier = Modifier
            .onFocusChanged {
                if (it.isFocused) showCategoryPicker.value = false
            }
            .fillMaxWidth(),
        onDone = {}
    )
}