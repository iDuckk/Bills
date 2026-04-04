package com.billsAplication.presentation.analytics.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.extension.getColorFromAttr

@Composable
fun ButtonAnalytics(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit) {
    val context = LocalContext.current

    Text(
        text = text,
        modifier = Modifier
            .width(80.dp)
            .height(30.dp)
            .border(width = 1.dp, color = color, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = if (isSelected) color else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                onClick.invoke()
            }
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center,
        color = if (isSelected)
            Color(context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
        else
            color,
        fontSize = 11.sp,
    )
}