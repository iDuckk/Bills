package com.billsAplication.presentation.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun ListCategory(
    chartColorsState: MutableState<List<Color>>,
    chartCategoryState: MutableState<List<String>>,
    bigDp: Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = bigDp)
    ) {
        chartCategoryState.value.forEachIndexed { index, category ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "",
                    modifier = Modifier
                        .padding(start = bigDp, end = bigDp)
                        .width(bigDp)
                        .height(bigDp)
                        .background(
                            color = chartColorsState.value[index],
                            shape = RoundedCornerShape(bigDp)
                        )
                )
                Text(
                    color = Color.Gray,
                    fontSize = 13.sp,
                    text = category
                )
            }
        }
    }
}