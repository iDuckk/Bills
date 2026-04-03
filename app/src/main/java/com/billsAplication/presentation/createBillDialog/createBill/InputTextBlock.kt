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
import com.billsAplication.presentation.components.AutoCompleteInputText
import com.billsAplication.presentation.components.InputText

@Composable
fun InputTextBlock(
    amount: MutableState<TextFieldValue>,
    note: MutableState<TextFieldValue>,
    description: MutableState<String>,
    isEditable: Boolean,
    type: Int,
    showCategoryPicker: MutableState<Boolean>,
    suggestions: List<String> = emptyList()
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

    // Note with AutoComplete
    AutoCompleteInputText(
        label = stringResource(R.string.title_note),
        value = note,
        suggestions = suggestions,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
        ),
        isEditable = isEditable,
        type = type,
        modifier = Modifier
            .onFocusChanged {
                if (it.isFocused) showCategoryPicker.value = false
            }
            .fillMaxWidth(),
        onValueChange = { newValue ->
            if (newValue.text.length <= 50) {
                note.value = newValue
            }
        }
    )

    // Description
    InputText(
        label = stringResource(R.string.title_description),
        value = description.value,
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
            description.value = newText
        },
        onDone = {}
    )
}