package com.billsAplication.presentation.shopList.view.bottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.billsAplication.presentation.shopList.ShopListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    viewModel: ShopListViewModel,
    showBottomSheet: MutableState<Boolean>,
    colorState: MutableState<String>,
    text: MutableState<String>,
    idState: MutableState<Int>,
    spacerType: Dp
) {
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                colorState.value = ""
                text.value = ""
                showBottomSheet.value = false
            },
            sheetState = sheetState,
            modifier = Modifier
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = spacerType)
            ) {

                ColorBoxes(
                    colorState = colorState
                )

                TextFieldNote(
                    colorState = colorState,
                    text = text,
                    spacerType = spacerType
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()

                ) {

                    ButtonAddNote(
                        viewModel = viewModel,
                        showBottomSheet = showBottomSheet,
                        colorState = colorState,
                        text = text,
                        idState = idState,
                        spacerType = spacerType
                    )

                    ButtonCancelNote(
                        showBottomSheet = showBottomSheet,
                        colorState = colorState,
                        text = text,
                        spacerType = spacerType
                    )

                    ButtonDeleteNote(
                        viewModel = viewModel,
                        showBottomSheet = showBottomSheet,
                        colorState = colorState,
                        text = text,
                        idState = idState,
                        spacerType = spacerType
                    )
                }

            }
        }
    }
}