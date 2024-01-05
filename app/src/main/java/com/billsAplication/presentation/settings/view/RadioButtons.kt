package com.billsAplication.presentation.settings.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.billsAplication.R

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RadioButtons(
    selectedOption: MutableState<String>,
    onOptionSelected: (String) -> Unit,
    color: Color,
    padding: Dp,
) {
    val radioOptions = listOf(
        stringResource(R.string.code_currency),
        stringResource(R.string.symbol_currency)
    )

    radioOptions.forEach { text ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentWidth()
                .selectable(
                    selected = (text == selectedOption.value),
                    onClick = {
                        onOptionSelected.invoke(text)
                    }
                )
        ) {
            RadioButton(
                colors = RadioButtonDefaults.colors(
                    selectedColor = color
                ),
                selected = (text == selectedOption.value),
                onClick = {
                    onOptionSelected.invoke(text)
                }
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = padding, end = padding)
            )
        }
    }
}