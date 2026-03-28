package com.billsAplication.presentation.createBillDialog.createBill

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.billsAplication.R
import com.billsAplication.utils.CurrentCurrency
import com.billsAplication.utils.StateColorButton.Companion.getColorType

@Composable
fun CurrencyInputField(
    value: TextFieldValue,
    type: MutableState<Int>,
    isEditable: MutableState<Boolean>,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFormatting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (isFormatting) return@OutlinedTextField

            val raw = newValue.text.replace(",", "")
            val parts = raw.split(".")

            val integerPart = parts.getOrNull(0)?.filter { it.isDigit() } ?: ""
            val decimalPart = parts.getOrNull(1)?.filter { it.isDigit() } ?: ""

            val formattedInteger = integerPart
                .reversed()
                .chunked(3)
                .joinToString(",")
                .reversed()

            val formatted = if (raw.contains('.')) {
                "$formattedInteger.${decimalPart.take(2)}"
            } else {
                formattedInteger
            }

            isFormatting = true
            onValueChange(
                TextFieldValue(
                    text = formatted.take(20),
                    selection = TextRange(formatted.take(20).length) // курсор в конец
                )
            )
            isFormatting = false
        },
        label = { Text(stringResource(R.string.title_amount)) },
        enabled = isEditable.value,
        trailingIcon = {
            Text(
                text = CurrentCurrency.currency,
                color = Color.DarkGray
            )
        },
        singleLine = true,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = getColorType(context = context, type = type.value),
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray,
            errorBorderColor = Color.Red,
            focusedLabelColor = getColorType(context = context, type = type.value),
            unfocusedLabelColor = Color.Gray,
            disabledLabelColor = Color.Gray,
            errorLabelColor = Color.Red,
            disabledTextColor = Color.DarkGray,
            focusedTextColor = Color.DarkGray,
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
    )
}
