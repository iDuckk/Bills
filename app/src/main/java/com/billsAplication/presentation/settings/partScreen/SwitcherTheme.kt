package com.billsAplication.presentation.settings.partScreen

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.billsAplication.presentation.settings.view.SwitchTheme
import com.billsAplication.utils.getColorFromAttr
import com.google.android.material.R

@Composable
fun SwitcherTheme(
    padding: Dp,
    defaultTheme: Boolean,
    color: Color,
    check: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val bg_color =
        Color(context.getColorFromAttr(R.attr.colorBackgroundFloating))
    val txt_color =
        Color(context.getColorFromAttr(R.attr.colorPrimaryVariant))

    Text(
        color = txt_color,
        fontSize = 12.sp,
        text = stringResource(com.billsAplication.R.string.themes_title)
    )
    Spacer(Modifier.size(padding))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(com.billsAplication.R.drawable.ic_light_theme),
            contentDescription = null
        )
        SwitchTheme(
            defaultTheme = defaultTheme,
            padding = padding,
            color = color,
            bg_color = bg_color,
            check = {
                check.invoke(it)
            }
        )
        Image(
            painter = painterResource(com.billsAplication.R.drawable.ic_night_theme),
            contentDescription = null
        )
    }
}