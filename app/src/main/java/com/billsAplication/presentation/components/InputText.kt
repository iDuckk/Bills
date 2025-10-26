package com.billsAplication.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import com.billsAplication.utils.StateColorButton.Companion.getColorType

@Composable
fun InputText(
    label: String,
    textStyle: TextStyle = LocalTextStyle.current.copy(),
    value: MutableState<String>,
    isError: MutableState<Boolean> = mutableStateOf(false),
    trailingIcon: @Composable() (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions,
    isEditable: MutableState<Boolean>,
    type: MutableState<Int>,
    readOnly: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = { value.value = it },
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value.value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        keyboardOptions = keyboardOptions,
        enabled = isEditable.value,
        readOnly = readOnly,
        singleLine = true,
        textStyle = textStyle,
        isError = isError.value,
        trailingIcon = trailingIcon,
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
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onDone.invoke()
            }
        ),
        modifier = modifier
    )
}
