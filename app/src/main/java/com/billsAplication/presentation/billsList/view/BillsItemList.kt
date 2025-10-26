package com.billsAplication.presentation.billsList.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.utils.CurrentCurrency
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_5
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BillsItemList(
    listDeleteItems: SnapshotStateList<List<BillsItem>>,
    item: BillsItem,
    onClick: (BillsItem) -> Unit,
    onLongClick: (BillsItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(DP_10))
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClick.invoke(item)
                },
                onLongClick = {
                    onLongClick.invoke(item)
                }
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(DP_10)
                .fillMaxWidth()
        ) {

            Text(
                text = item.category,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Box(
                Modifier
                    .padding(start = DP_10, end = DP_10, bottom = (3.5).dp)
                    .height(1.dp)
                    .weight(1f)
                    .align(Alignment.Bottom)
                    .background(Color.Gray, shape = DottedShape(step = DP_5))
            )

            Text(
                textAlign = TextAlign.End,
                text = item.amount + " " + CurrentCurrency.currency,
                maxLines = 1,
                fontSize = 14.sp,
                color = if (item.type == BillsItem.TYPE_EXPENSES)
                    colorResource(id = R.color.text_expense)
                else
                    colorResource(id = R.color.text_income),
                modifier = Modifier.align(Alignment.Bottom)
            )

            if (listDeleteItems.isNotEmpty()) {
                if (listDeleteItems.first().contains(item)) {
                    Box(
                        modifier = Modifier
                            .padding(start = DP_10)
                            .height(DP_10)
                            .width(DP_10)
                            .background(Color.Red, shape = CircleShape)
                    ) {
                    }
                }
            }
        }
    }
}

private data class DottedShape(
    val step: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(Path().apply {
        val stepPx = with(density) { step.toPx() }
        val stepsCount = (size.width / stepPx).roundToInt()
        val actualStep = size.width / stepsCount
        val dotSize = Size(width = actualStep / 2, height = size.height)
        for (i in 0 until stepsCount) {
            addRect(
                Rect(
                    offset = Offset(x = i * actualStep, y = 0f),
                    size = dotSize
                )
            )
        }
        close()
    })
}


@Preview(showBackground = true)
@Composable
fun BillsItemList() {
    BillsItemList(
        listDeleteItems = SnapshotStateList(),
        onClick = {},
        onLongClick = {},
        item = BillsItem(
            id = 0,
            time = "02:02 PM",
            date = "10/01/2024 02:02 PM",
            category = "Food",
            note = "sweets",
            amount = "154.56 $",
            month = "",
            image1 = "asd",
            type = BillsItem.TYPE_EXPENSES
        )
    )
}