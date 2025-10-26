package com.billsAplication.presentation.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.presentation.components.MonthPicker.Companion.NEXT_MONTH
import com.billsAplication.presentation.components.MonthPicker.Companion.PREV_MONTH
import com.billsAplication.presentation.components.MonthPicker.Companion.changeMonth
import com.billsAplication.presentation.components.MonthPicker.Companion.changeYear
import com.billsAplication.presentation.components.MonthPicker.Companion.date
import com.billsAplication.utils.MapMonth
import com.billsAplication.utils.PagingConstants.DP_10
import java.time.LocalDate

class MonthPicker() {
    companion object {
        val NEXT_MONTH = true
        val PREV_MONTH = false

        var changeMonth = 0
        var changeYear = 0

        var date = LocalDate.now()
    }
}

@Composable
fun MonthPicker(
    month: MutableState<String>
) {
    val context = LocalContext.current

    if (month.value == "") month.value = currentDate(date = date, context = context)

    Row(
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(.45f)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic__navigate_before),
            contentDescription = stringResource(R.string.description_previous_month),
            modifier = Modifier
                .clip(RoundedCornerShape(DP_10))
                .clickable {
                    month.value = changeMonthBar(typeChanges = PREV_MONTH, context = context)
                }
        )
        Text(
            maxLines = 1,
            text = month.value,
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(DP_10))
                .weight(1f)
                .clickable {
                    defaultMonth()
                    date = LocalDate.now()
                    month.value = currentDate(date = date, context = context)
                },
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(id = R.drawable.ic_navigate_next),
            contentDescription = stringResource(R.string.description_next_month),
            modifier = Modifier
                .clip(RoundedCornerShape(DP_10))
                .clickable {
                    month.value = changeMonthBar(typeChanges = NEXT_MONTH, context = context)
                }
        )
    }
}

fun defaultMonth() {
    changeMonth = 0
    changeYear = 0
}

fun changeMonthBar(typeChanges : Boolean, context: Context) : String {
    if (typeChanges) changeMonth++ else changeMonth--
    //Set Year
    if(date.month.plus(changeMonth.toLong()).toString() == MapMonth.JANUARY
        && typeChanges
    ) changeYear++
    else if(date.month.plus(changeMonth.toLong()).toString() == MapMonth.DECEMBER
        && !typeChanges
    ) changeYear--
    //Set Month
    if (changeMonth > 0) {
        return MapMonth.mapMonthToTextView(date.month.plus(changeMonth.toLong()).toString(), context) +
                " " + date.year.plus(Math.abs(changeYear).toLong()).toString()
    }
    else {
        return MapMonth.mapMonthToTextView(date.month.minus(Math.abs(changeMonth).toLong()).toString(), context) +
                " " + date.year.minus(Math.abs(changeYear).toLong()).toString()
    }
}

fun currentDate(date: LocalDate, context: Context) : String {
    return MapMonth.mapMonthToTextView(date.month.toString(), context) + " " + date.year.toString()
}