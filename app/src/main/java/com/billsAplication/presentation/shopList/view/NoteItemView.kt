package com.billsAplication.presentation.shopList.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.domain.model.NoteItem
import com.billsAplication.extension.getColorFromAttr

@Composable
fun NoteItemView(
    note: NoteItem,
    bigDp: Dp,
    selectItem: (Int) -> Unit
) {

    val context = LocalContext.current

    Card(
        shape = RectangleShape,
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        colors = CardDefaults.cardColors(
            containerColor = if (note.color == "")
                Color(context.getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating))
            else
                Color(android.graphics.Color.parseColor(note.color))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .defaultMinSize(minHeight = 100.dp)
            .padding(top = 3.dp, bottom = 2.dp)
            .clickable {
                selectItem.invoke(note.id)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bigDp)
        ) {
            Text(
                text = note.textNote,
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 1.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Test() {
    NoteItemView(
        note = NoteItem(
            textNote = "sweets",
            color = "",
            dateTimeCreation = ""
        ),
        bigDp = 10.dp
    ) {

    }
}