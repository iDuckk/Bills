package com.billsAplication.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ToggleBtn(
    label: String,
    type: MutableState<Int>,
    isEditable: MutableState<Boolean>? = null,
    newType: Int,
    newColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = {
            if (type.value != newType) {
                if (type.value != newType) {
                    if (isEditable == null) {
                        type.value = newType
                        onClick.invoke()
                    } else {
                        if(isEditable.value) {
                            type.value = newType
                            onClick.invoke()
                        }
                    }
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = if (type.value == newType) Color.White else newColor,
            containerColor = if (type.value == newType) newColor else Color.Transparent,
        ),
        border = BorderStroke(1.dp, newColor),
    ) {
        Text(label)
    }
}