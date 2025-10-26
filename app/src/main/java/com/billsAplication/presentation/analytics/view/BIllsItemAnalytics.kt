package com.billsAplication.presentation.analytics.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.utils.CurrentCurrency

@Composable
fun BIllsItemAnalytics(
    item: BillsItem,
    smallDp: Dp,
    bigDp: Dp
) {
    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 1.dp)
    ) {
        Divider(
            color = Color.DarkGray,
            thickness = 1.dp
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bigDp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.date,
                    maxLines = 1,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.wrapContentWidth()
                )
                Text(
                    text = item.category,
                    maxLines = 1,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.wrapContentWidth()
                )
            }
            Row(
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = smallDp)
            ) {
                if (pictureExist(item = item)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_image),
                        contentDescription = null
                    )
                }
                Text(
                    text = item.amount  + " " + CurrentCurrency.currency,
                    maxLines = 1,
                    fontSize = 14.sp,
                    color = if (item.type == BillsItem.TYPE_EXPENSES)
                        colorResource(id = R.color.text_expense)
                    else
                        colorResource(id = R.color.text_income),
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 30.dp)
                )
                Text(
                    text = item.note,
                    fontSize = 13.sp,
                    maxLines = 1,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

private fun pictureExist(item: BillsItem) =
    item.image1.isNotEmpty() ||
            item.image2.isNotEmpty() ||
            item.image3.isNotEmpty() ||
            item.image4.isNotEmpty() ||
            item.image4.isNotEmpty()

@Preview(showBackground = true)
@Composable
fun BIllsItemAnalytics() {
    BIllsItemAnalytics(
        item = BillsItem(
            id = 0,
            time = "",
            date = "10/01/2024 02:02 PM",
            category = "Food",
            note = "sweets",
            amount = "154.56 $",
            month = "",
            image1 = "asd",
            type = BillsItem.TYPE_EXPENSES),
        smallDp = 5.dp,
        bigDp = 10.dp
    )
}
