package com.billsAplication.presentation.analytics.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.presentation.analytics.AnalyticsViewModel

@Composable
fun MonthPicker(
    viewModel: AnalyticsViewModel,
    month: MutableState<String>,
    padding: Dp
) {
    val NEXT_MONTH = true
    val PREV_MONTH = false

    Image(
        painter = painterResource(id = R.drawable.ic__navigate_before),
        contentDescription = null,
        modifier = Modifier
            .clickable {
                viewModel.currentDate = viewModel.changeMonthBar(PREV_MONTH)
                month.value = viewModel.currentDate //Set month`s text in bar
            }
    )
    Spacer(Modifier.size(padding))
    Text(
        maxLines = 1,
        text = month.value,
        color = Color.Gray,
        fontSize = 13.sp,
        modifier = Modifier
            .wrapContentSize()
            .clickable {
                month.value = viewModel.currentDate()
                viewModel.defaultMonth()
            },
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.size(padding))
    Image(
        painter = painterResource(id = R.drawable.ic_navigate_next),
        contentDescription = null,
        modifier = Modifier
            .clickable {
                viewModel.currentDate = viewModel.changeMonthBar(NEXT_MONTH)
                month.value = viewModel.currentDate //Set month`s text in bar
            }
    )
}