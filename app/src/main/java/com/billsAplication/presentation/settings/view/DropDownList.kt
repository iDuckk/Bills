package com.billsAplication.presentation.settings.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.utils.getColorFromAttr


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownList(
    list: List<String>,
    modifier: Modifier,
    color: Color,
    currencyPos: Int,
    onSet: (Int) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(list[currencyPos]) }

    val bg_color = Color(context.getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating))
    val txt_color = Color(context.getColorFromAttr(com.google.android.material.R.attr.colorPrimaryVariant))

    Box(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier
                .border(1.dp, color, RoundedCornerShape(10.dp))
                .fillMaxSize(),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = bg_color,
                    unfocusedContainerColor = bg_color,
                    disabledContainerColor = bg_color,
                    focusedIndicatorColor = bg_color,
                    unfocusedIndicatorColor = bg_color,
                    disabledIndicatorColor = bg_color,
                    unfocusedTextColor = txt_color,
                    focusedTextColor = txt_color
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
//                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .wrapContentSize()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                LazyColumn (modifier = Modifier.height(250.dp).width(240.dp)) {
                    itemsIndexed(list) { index, item ->
                        DropdownMenuItem(
                            text = { Text(
                                color = if (selectedText == item) color else Color.Black,
                                text = item
                            ) },
                            onClick = {
                                selectedText = item
                                expanded = false
                                onSet.invoke(index)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropMenu() {
    DropDownList(
        list = listOf("Dollar", "Rubble", "Pesso"),
        modifier = Modifier
            .height(60.dp)
            .width(180.dp)
            .wrapContentHeight()
            .wrapContentWidth()
            .padding(5.dp),
        color = Color.Red,
        currencyPos = 1
    ){}
}