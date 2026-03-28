package com.billsAplication.presentation.createBillDialog.createBill

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.presentation.components.ImgBtn
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.StateColorButton.Companion.getColorType

@Composable
fun TopBlock(
    type: MutableState<Int>,
    shownBookmark: Boolean,
    isEditable: MutableState<Boolean>,
    showCategoryPicker: MutableState<Boolean>,
    bookmark: MutableState<Boolean>,
    onBack: () -> Unit,
    onBookmark: () -> Unit
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = DP_10)
    ) {
        ImgBtn(
            src = painterResource(id = R.drawable.ic_arrow_back),
            description = stringResource(R.string.back_to_set_type_bill)
        ) {
            onBack.invoke()
        }

        Spacer(Modifier.width(DP_10))

        Text(
            text = stringResource(R.string.title_new_bill),
            fontSize = 18.sp
        )

        Spacer(Modifier.weight(1f))

        Text(stringResource(R.string.edit))

        Checkbox(
            colors = CheckboxDefaults.colors(
                uncheckedColor = getColorType(context = context, type = type.value),
                checkedColor = getColorType(context = context, type = type.value)
            ),
            checked = isEditable.value, onCheckedChange = {
                isEditable.value = it
                showCategoryPicker.value = false
            })

        Spacer(Modifier.width(DP_10))

        if (shownBookmark) {
            ImgBtn(
                src = if (bookmark.value) painterResource(id = R.drawable.ic_bookmark_enable)
                else painterResource(id = R.drawable.ic_bookmark_disable),
                description = stringResource(id = R.string.title_bookmarks)
            ) {
                bookmark.value = !bookmark.value
                onBookmark.invoke()
            }
        }

    }
}