package com.billsAplication.presentation.shopList.view.bottomSheet

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.billsAplication.R

@Composable
fun TextFieldNote(
    colorState: MutableState<String>,
    text: MutableState<String>,
    spacerType: Dp
) {
    TextField(
        colors = TextFieldDefaults.colors(
            focusedContainerColor = if (colorState.value != "") Color(
                android.graphics.Color.parseColor(
                    colorState.value
                )
            ) else Color.Transparent,
            unfocusedContainerColor = if (colorState.value != "") Color(
                android.graphics.Color.parseColor(
                    colorState.value
                )
            ) else Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.LightGray,
            focusedLabelColor = Color.Transparent,
            unfocusedLabelColor = if (text.value == "") Color.LightGray else Color.Transparent
        ),
        value = text.value,
        onValueChange = {
            text.value = it
        },
        label = { Text(stringResource(id = R.string.type_note)) },
        modifier = Modifier
            .padding(spacerType)
            .border(width = 1.dp, color = Color.LightGray)
            .fillMaxWidth()
            .height(200.dp)
    )
}