package com.billsAplication.presentation.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_EXPENSES
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_INCOME
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_EXPENSES
import com.billsAplication.extension.convertDateToMonthYear
import com.billsAplication.extension.convertTo12HourFormat
import com.billsAplication.extension.convertTo24HourFormat
import com.billsAplication.presentation.dialogs.createBill.AddBtnBlock
import com.billsAplication.presentation.dialogs.createBill.CategoryBlock
import com.billsAplication.presentation.dialogs.createBill.DateTimeBlock
import com.billsAplication.presentation.dialogs.createBill.InputTextBlock
import com.billsAplication.presentation.dialogs.createBill.TopBlock
import com.billsAplication.presentation.dialogs.createBill.TypeBtnBlock
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_15
import com.billsAplication.utils.PagingConstants.DP_5


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillDialog(
    showDialog: MutableState<CreateBillDialog>,
    listCategory: MutableState<List<BillsItem>>,
    getListCategory: (Int) -> Unit,
    onAddBill: (BillsItem) -> Unit,
    onDismiss: () -> Unit,
    onDeleteCategory: (BillsItem) -> Unit
) {

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showDialog.value.show) {
        val bill = showDialog.value.bill

        val type = remember { mutableStateOf(bill?.type ?: TYPE_EXPENSES) }
        val isEditable = remember { mutableStateOf(bill == null) }
        val date = remember { mutableStateOf(bill?.date ?: "") }
        val time = remember { mutableStateOf(convertTo12HourFormat(bill?.time ?: "")) }
        val category = remember { mutableStateOf(bill?.category ?: "") }
        val newCategory = remember { mutableStateOf("") }
        val amount = remember { mutableStateOf(TextFieldValue(bill?.amount ?: "")) }
        val note = remember { mutableStateOf(bill?.note ?: "") }
        val description = remember { mutableStateOf(bill?.description ?: "") }
        var images by remember { mutableStateOf(listOf<String>()) }
        val showCategoryPicker = remember { mutableStateOf(false) }
        val bookmark = remember { mutableStateOf(false) }
        val showDatePicker = remember { mutableStateOf(false) }
        val showTimePicker = remember { mutableStateOf(false) }
        val showMainBlock = remember { mutableStateOf(bill != null) }
        val isErrorCategory = remember { mutableStateOf(false) }

        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = {
                showDialog.value = CreateBillDialog(show = false, bill = null)
                onDismiss.invoke()
            }) {
            val focusManager = LocalFocusManager.current

            Column(
                Modifier
                    .padding(DP_15)
                    .windowInsetsPadding(WindowInsets.ime)
            ) {
                if (showMainBlock.value) {
                    TopBlock(
                        type = type,
                        isEditable = isEditable,
                        showCategoryPicker = showCategoryPicker,
                        bookmark = bookmark,
                        shownBookmark = bill != null,
                        onBack = {
                            if (bill != null) {
                                showDialog.value = CreateBillDialog(show = false, bill = null)
                            } else {
                                showMainBlock.value = false
                            }
                        },
                        onBookmark = {
                            showCategoryPicker.value = false
                            focusManager.clearFocus()
                            if (category.value.isNotEmpty()) {
                                onAddBill(
                                    BillsItem(
                                        id = 0,
                                        type = type.value,
                                        month = convertDateToMonthYear(date.value),
                                        date = date.value,
                                        time = convertTo24HourFormat(time.value),
                                        category = category.value,
                                        amount = amount.value.text.ifEmpty { "0.0" },
                                        note = note.value,
                                        description = description.value,
                                        bookmark = true,
                                        image1 = images.getOrNull(0) ?: "",
                                        image2 = images.getOrNull(1) ?: "",
                                        image3 = images.getOrNull(2) ?: "",
                                        image4 = images.getOrNull(3) ?: "",
                                        image5 = images.getOrNull(4) ?: ""
                                    )
                                )
                                showDialog.value = CreateBillDialog(show = false, bill = null)
                                onDismiss()
                            } else {
                                isErrorCategory.value = true
                            }
                        }
                    )

                    DateTimeBlock(
                        date = date,
                        time = time,
                        isEditable = isEditable,
                        type = type,
                        showDatePicker = showDatePicker,
                        showTimePicker = showTimePicker,
                        showCategoryPicker = showCategoryPicker,
                    )

                    CategoryBlock(
                        isErrorCategory = isErrorCategory,
                        listCategory = listCategory,
                        category = category,
                        newCategory = newCategory,
                        isEditable = isEditable,
                        type = type,
                        showCategoryPicker = showCategoryPicker,
                        onAddNewCategory = { newItem ->
                            onAddBill(newItem)
                        },
                        onDeleteCategory = { bill ->
                            onDeleteCategory.invoke(bill)
                        }
                    )
//TODO Убрать AUTOCOMPLETE текст NOTE
                    InputTextBlock(
                        amount = amount,
                        note = note,
                        description = description,
                        isEditable = isEditable,
                        type = type,
                        showCategoryPicker = showCategoryPicker,
                    )

                    Spacer(Modifier.height(DP_10))

                    Row {
                        IconButton(onClick = { /* Выбрать фото с диска */ }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_attach_file),
                                contentDescription = stringResource(R.string.take_a_photo)
                            )
                        }
                        IconButton(onClick = { /* Сделать фото с камеры */ }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_add_a_photo),
                                contentDescription = stringResource(R.string.take_a_photo)
                            )
                        }
                    }

                    LazyRow {
                        items(images.take(5)) { imagePath ->
                            Image(
                                painter = rememberAsyncImagePainter(imagePath),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(DP_5)
                            )
                        }
                    }

                    Spacer(Modifier.height(DP_15))

                    AddBtnBlock(
                        bill = bill,
                        type = type,
                        date = date,
                        time = time,
                        category = category,
                        amount = amount,
                        note = note,
                        description = description,
                        images = images,
                        onClick = { newItem ->
                            showCategoryPicker.value = false
                            focusManager.clearFocus()
                            if (category.value.isNotEmpty()) {
                                onAddBill(newItem)
                                showDialog.value = CreateBillDialog(show = false, bill = null)
                                onDismiss()
                            } else {
                                isErrorCategory.value = true
                            }
                        }
                    )
                } else {
                    //Set default values
                    type.value = TYPE_EXPENSES
                    isEditable.value = true
                    date.value = ""
                    time.value = ""
                    category.value = ""
                    newCategory.value = ""
                    amount.value = TextFieldValue("")
                    note.value = ""
                    description.value = ""
                    images = listOf()
                    showCategoryPicker.value = false
                    bookmark.value = false
                    showDatePicker.value = false
                    showMainBlock.value = false

                    TypeBtnBlock(
                        type = type,
                        showMainBlock = showMainBlock,
                        onClick = {
                            getListCategory.invoke(
                                if (type.value == TYPE_EXPENSES) TYPE_CATEGORY_EXPENSES else TYPE_CATEGORY_INCOME
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoBlock() {

}

data class CreateBillDialog(val show: Boolean, val bill: BillsItem?)



