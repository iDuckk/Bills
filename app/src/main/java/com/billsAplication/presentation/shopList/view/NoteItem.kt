package com.billsAplication.presentation.shopList.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.domain.model.NoteItem
import com.billsAplication.extension.getColorFromAttr

@Composable
fun NoteItem(
    note: NoteItem,
    bigDp: Dp
) {

    val context = LocalContext.current

    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = if (note.color == "")
                Color(context.getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating))
            else
                Color(android.graphics.Color.parseColor(note.color))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .defaultMinSize(minHeight = 100.dp)
            .padding(top = 3.dp, bottom = 2.dp)
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
            Image(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Test() {
    NoteItem(
        note = NoteItem(
            textNote = "sweets",
            color = "",
            dateTimeCreation = ""
        ),
        bigDp = 10.dp
    )
}