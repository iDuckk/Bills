package com.billsAplication.presentation.shopList.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.Dp
import com.billsAplication.presentation.shopList.ShopListViewModel

@Composable
fun NotesList(
    viewModel: ShopListViewModel,
    showBottomSheet: MutableState<Boolean>,
    colorState: MutableState<String>,
    text: MutableState<String>,
    idState: MutableState<Int>,
    spacerType: Dp
) {
    val notes = viewModel.notes().collectAsState(initial = listOf())
    LazyColumn(content = {
        items(items = notes.value) {
            NoteItemView(note = it, bigDp = spacerType) { id ->
                idState.value = id
                colorState.value = it.color
                text.value = it.textNote
                showBottomSheet.value = true
            }
        }
    })
}