package com.billsAplication.presentation.bookmarks.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.utils.CurrentCurrency
import com.billsAplication.utils.PagingConstants.DP_10

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkItem(
    item: BillsItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(DP_10))
            .fillMaxWidth()
            .background(if (isSelected) Color.LightGray.copy(alpha = 0.3f) else Color.Transparent)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(DP_10),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                if (item.note.isNotEmpty()) {
                    Text(
                        text = item.note,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = "${item.amount} ${CurrentCurrency.currency}",
                fontSize = 16.sp,
                color = if (item.type == BillsItem.TYPE_EXPENSES)
                    colorResource(id = R.color.text_expense)
                else
                    colorResource(id = R.color.text_income)
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .padding(start = DP_10)
                        .size(10.dp)
                        .background(Color.Red, shape = CircleShape)
                )
            }
        }
    }
}
