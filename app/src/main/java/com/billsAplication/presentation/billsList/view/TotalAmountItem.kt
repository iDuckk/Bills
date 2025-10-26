package com.billsAplication.presentation.billsList.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R

@Composable
fun TotalAmountItem(
    smallDp: Dp,
    bigDp: Dp,
    amount: String
    ) {
    Column(
        Modifier.padding(top = bigDp + smallDp, end = bigDp)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.bill_list_total),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(end = smallDp)
            )
            Text(
                text = amount,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TotalAmountItem() {
    TotalAmountItem(
        smallDp = 5.dp,
        bigDp = 10.dp,
        amount = "154.56 $"
    )
}
