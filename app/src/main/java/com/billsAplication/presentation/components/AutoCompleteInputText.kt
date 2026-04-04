package com.billsAplication.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties

@Composable
fun AutoCompleteInputText(
    label: String,
    value: MutableState<TextFieldValue>,
    suggestions: List<String>,
    isEditable: Boolean,
    type: Int,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (TextFieldValue) -> Unit = { value.value = it },
    onDone: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    
    val filteredSuggestions = remember(value.value.text, suggestions) {
        if (value.value.text.isNotEmpty()) {
            suggestions.filter { 
                it.contains(value.value.text, ignoreCase = true) && it != value.value.text 
            }
        } else {
            emptyList()
        }
    }

    Column(modifier = modifier.onGloballyPositioned { coordinates ->
        textFieldSize = coordinates.size.toSize()
    }) {
        InputTextValue(
            label = label,
            value = value.value,
            isEditable = isEditable,
            type = type,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            onValueChange = {
                onValueChange(it)
                expanded = filteredSuggestions.isNotEmpty()
            },
            onDone = onDone
        )

        if (filteredSuggestions.isNotEmpty()) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                properties = PopupProperties(focusable = false)
            ) {
                filteredSuggestions.take(5).forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(text = suggestion) },
                        onClick = {
                            // Устанавливаем текст и переносим курсор в конец
                            onValueChange(
                                TextFieldValue(
                                    text = suggestion,
                                    selection = TextRange(suggestion.length)
                                )
                            )
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
