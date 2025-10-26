package com.billsAplication.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.extension.getColorFromAttr
import com.billsAplication.utils.PagingConstants.DP_5
import com.billsAplication.utils.StateColorButton

@Composable
fun BoxScope.AddButton(
    painterResource: MutableIntState,
    typeColorAddButton: MutableIntState,
    application: BillsApplication,
    onCLick: () -> Unit,
) {
    val gradient = StateColorButton(application = application).gradientButton(typeColorAddButton.intValue)
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            colorResource(R.color.text_income),
            colorResource(R.color.text_expense)
        ), // Gradient colors
        startX = gradient[0], // Starting Y position of the gradient
        endX = gradient[1] // Ending Y position of the gradient
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(bottom = 25.dp, end = 20.dp)
            .shadow(elevation = DP_5, shape = CircleShape)
            .align(Alignment.BottomEnd)
            .clip(shape = CircleShape)
            .clickable(onClick = {
                onCLick.invoke()
            })
            .size(56.dp)
            .background(brush = gradientBrush),
        content = {
            Icon(
                painter = painterResource(id = painterResource.value),
                contentDescription = stringResource(id = R.string.choose_type_of_add_note),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    )
}