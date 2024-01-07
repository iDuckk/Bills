package com.billsAplication.presentation.settings.view

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun SwitchTheme(
    defaultTheme: Boolean,
    padding: Dp,
    color: Color,
    bg_color: Color,
    check: (Boolean)-> Unit
    ) {
    var checked by remember { mutableStateOf(defaultTheme) }

    Switch(
        checked = checked,
        modifier = Modifier.padding(start = padding, end = padding),
        colors = SwitchDefaults.colors(
            checkedThumbColor = color,
            checkedTrackColor = bg_color,
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedTrackColor = bg_color,
            checkedBorderColor = color,
            uncheckedBorderColor = color
        ),
        onCheckedChange = {
            checked = it
            check.invoke(it)
        }
    )
}