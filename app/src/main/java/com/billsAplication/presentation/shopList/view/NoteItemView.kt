package com.billsAplication.presentation.shopList.view

import android.text.Layout.Alignment
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Patterns
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import com.billsAplication.R
import com.billsAplication.domain.model.NoteItem
import com.billsAplication.extension.getColorFromAttr

@Composable
fun NoteItemView(
    note: NoteItem,
    bigDp: Dp,
    selectItem: (Int) -> Unit
) {

    val context = LocalContext.current
    val customLinkifyTextView = remember {
        TextView(context)
    }

    Card(
        shape = RoundedCornerShape(13.dp),
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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bigDp)
        ) {
            AndroidView(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                factory = { customLinkifyTextView }
            ) { textView ->
                textView.textSize = 24f
                textView.textAlignment = TEXT_ALIGNMENT_CENTER
                textView.setLinkTextColor(context.getColor(R.color.text_income))
                textView.text = note.textNote ?: ""
                LinkifyCompat.addLinks(textView, Linkify.WEB_URLS)
                Linkify.addLinks(textView, Patterns.WEB_URL, "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)")
                textView.movementMethod = LinkMovementMethod.getInstance()
            }
//            Text(
//                text = note.textNote,
//                color = Color.Gray,
//                fontSize = 18.sp,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(end = 1.dp)
//            )
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