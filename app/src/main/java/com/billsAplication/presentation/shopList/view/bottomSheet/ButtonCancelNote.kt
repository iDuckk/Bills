package com.billsAplication.presentation.shopList.view.bottomSheet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.billsAplication.R

@Composable
fun ButtonCancelNote(
    showBottomSheet: MutableState<Boolean>,
    colorState: MutableState<String>,
    text: MutableState<String>,
    spacerType: Dp
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.default_background)
        ),
        modifier = Modifier
            .padding(spacerType),
        onClick = {
            colorState.value = ""
            text.value = ""
            showBottomSheet.value = false
        }
    ) {
        Text(
            text = stringResource(id = R.string.search_cancel)
        )
    }
}