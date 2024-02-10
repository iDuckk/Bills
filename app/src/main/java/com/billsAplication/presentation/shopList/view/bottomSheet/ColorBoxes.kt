package com.billsAplication.presentation.shopList.view.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.billsAplication.R

@Composable
fun ColorBoxes(
    colorState: MutableState<String>,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()

    ) {
        listOf<String>(
            "#FF8A80", "#FDD835", "#82B1FF",
            "#FB8C00", "#69F0AE", "#B388FF",
            ""
        ).forEach {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .padding(5.dp)
                    .border(
                        width = 1.dp,
                        color = if (it != "") Color.Transparent else Color.LightGray,
                        shape = CircleShape
                    )
                    .background(
                        color = if (it != "")
                            Color(android.graphics.Color.parseColor(it))
                        else
                            Color.Transparent,
                        shape = CircleShape
                    )
                    .clip(shape = CircleShape)
                    .clickable {
                        colorState.value = if (it != "")
                            it
                        else
                            ""
                    },
                content = {
                    if (it == "")
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                }
            )
        }
    }
}