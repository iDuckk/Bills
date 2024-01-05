package com.billsAplication.presentation.settings.view

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ButtonSettings(
    text: String,
    color: Color,
    onClick: () -> Unit) {
    Button(
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        onClick = {
            onClick.invoke()
        }) {
        Text(
            text = text
        )
    }
}