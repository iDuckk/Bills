package com.billsAplication.presentation.settings.partScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.presentation.settings.SettingsFragment
import com.billsAplication.presentation.settings.view.DropDownList
import com.billsAplication.utils.getColorFromAttr
import com.google.android.material.R

@Composable
fun Language(
    padding: Dp,
    languageList: List<String>,
    color: Color,
    set: (Int) -> Unit
) {
    val context = LocalContext.current
    Text(
        color = Color(context.getColorFromAttr(R.attr.colorPrimaryVariant)),
        fontSize = 12.sp,
        text = stringResource(com.billsAplication.R.string.language)
    )
    Spacer(Modifier.size(padding))
    DropDownList(
        list = languageList,
        modifier = Modifier
            .height(60.dp)
            .width(240.dp),
        color = color,
        currencyPos = SettingsFragment.currentLanguagePosition,
        onSet = { position ->
            set.invoke(position)
        }
    )
}