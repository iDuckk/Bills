package com.billsAplication.presentation.settings.partScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.presentation.settings.view.DropDownList
import com.billsAplication.presentation.settings.view.RadioButtons
import com.billsAplication.extension.getColorFromAttr

@Composable
fun Currency(
    typeCurrency: Boolean,
    currencyPos: Int,
    padding: Dp,
    currencyList: List<String>,
    color: Color,
    onSet: (Int) -> Unit,
    onOptionSelected: (List<String>, String) -> Unit
    ) {
    val context = LocalContext.current
    val radioOptions = listOf(
        stringResource(R.string.code_currency),
        stringResource(R.string.symbol_currency)
    )
    val selectedOption = remember {
        mutableStateOf(
            radioOptions[
                if (typeCurrency) 1 else 0
            ]
        )
    }

    Column {
        Text(
            color = Color(context.getColorFromAttr(com.google.android.material.R.attr.colorPrimaryVariant)),
            fontSize = 12.sp,
            text = stringResource(R.string.currency)
        )
        Spacer(Modifier.size(padding))
        DropDownList(
            list = currencyList,
            modifier = Modifier
                .height(60.dp)
                .width(240.dp),
            color = color,
            currencyPos = currencyPos,
            onSet = { position ->
                onSet.invoke(position)
            }
        )
        Row(
            modifier = Modifier
                .padding(top = padding)
        ) {
            RadioButtons(
                radioOptions = radioOptions,
                selectedOption = selectedOption,
                onOptionSelected = { text ->
                    selectedOption.value = text
                    onOptionSelected.invoke(radioOptions, text)
                },
                color = color,
                txtColor = Color(context.getColorFromAttr(com.google.android.material.R.attr.colorPrimaryVariant)),
                padding = padding
            )
        }
    }
}