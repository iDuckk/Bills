package com.billsAplication.presentation.shopList.view.bottomSheet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.billsAplication.R
import com.billsAplication.domain.model.NoteItem
import com.billsAplication.presentation.shopList.ShopListViewModel

@Composable
fun ButtonDeleteNote(
    viewModel: ShopListViewModel,
    showBottomSheet: MutableState<Boolean>,
    colorState: MutableState<String>,
    text: MutableState<String>,
    idState: MutableState<Int>,
    spacerType: Dp
) {
    Button(
    colors = ButtonDefaults.buttonColors(
    containerColor = colorResource(id = R.color.text_expense)
    ),
    modifier = Modifier
    .padding(spacerType),
    onClick = {
        viewModel.deleteNote(
            NoteItem(
                id = idState.value,
                textNote = text.value,
                color = colorState.value,
            )
        )
        colorState.value = ""
        text.value = ""
        showBottomSheet.value = false
    }
    ) {
        Text(
            text = stringResource(id = R.string.b_delete_note)
        )
    }
}