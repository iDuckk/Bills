package com.billsAplication.presentation.shopList.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.billsAplication.R
import com.billsAplication.extension.getColorFromAttr

@Composable
fun BoxScope.ButtonKeyboardToText(
    color: Int,
    showBottomSheet: MutableState<Boolean>,
    idState: MutableState<Int>
) {

    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(bottom = 78.dp, end = 10.dp)
            .align(Alignment.BottomEnd)
            .clip(shape = CircleShape)
            .clickable {
                idState.value = 0
                showBottomSheet.value = true
            }
            .size(40.dp)
            .background(color = Color(color)),
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_keyboard),
                contentDescription = stringResource(id = R.string.add_note_with_keyboard),
                tint = Color(context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary)),
                modifier = Modifier.size(24.dp)
            )
        }
    )
}