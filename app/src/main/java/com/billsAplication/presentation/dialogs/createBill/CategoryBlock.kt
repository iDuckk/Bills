package com.billsAplication.presentation.dialogs.createBill

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_EXPENSES
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_INCOME
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_EXPENSES
import com.billsAplication.presentation.components.ImgBtn
import com.billsAplication.presentation.components.InputText
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_5

@Composable
fun CategoryBlock(
    isErrorCategory: MutableState<Boolean>,
    listCategory: MutableState<List<BillsItem>>,
    category: MutableState<String>,
    newCategory: MutableState<String>,
    isEditable: MutableState<Boolean>,
    type: MutableState<Int>,
    showCategoryPicker: MutableState<Boolean>,
    onAddNewCategory: (BillsItem) -> Unit,
    onDeleteCategory: (BillsItem) -> Unit
) {
    // Category
    InputText(
        label = stringResource(R.string.title_category),
        value = category,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        isEditable = isEditable,
        readOnly = true,
        type = type,
        isError = isErrorCategory,
        trailingIcon = {
            if (isErrorCategory.value)
                Icon(Icons.Filled.Info, stringResource(R.string.toast_fill_category), tint = MaterialTheme.colorScheme.error)
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) showCategoryPicker.value = true
            },
        onDone = {}
    )
    if (isErrorCategory.value) {
        Text(
            text = stringResource(R.string.toast_fill_category),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error
        )
    }

    if (showCategoryPicker.value) {
        NewCategoryCard(
            newCategory = newCategory,
            isEditable = isEditable,
            type = type,
            listCategory = listCategory,
            category = category,
            isErrorCategory = isErrorCategory,
            showCategoryPicker = showCategoryPicker,
            onAddNewCategory = onAddNewCategory,
            onDeleteCategory = onDeleteCategory
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NewCategoryCard(
    newCategory: MutableState<String>,
    isEditable: MutableState<Boolean>,
    type: MutableState<Int>,
    listCategory: MutableState<List<BillsItem>>,
    category: MutableState<String>,
    showCategoryPicker: MutableState<Boolean>,
    isErrorCategory: MutableState<Boolean>,
    onAddNewCategory: (BillsItem) -> Unit,
    onDeleteCategory: (BillsItem) -> Unit
) {
    val showDeleteBtn = remember { mutableStateOf(0) }
    val focusManager = LocalFocusManager.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(DP_10)
        ) {

            NewCategoryInputText(
                newCategory = newCategory,
                isEditable = isEditable,
                type = type,
                showDeleteBtn = showDeleteBtn,
                onAddNewCategory = onAddNewCategory
            )

            Spacer(Modifier.height(DP_10))

            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(listCategory.value.distinctBy { it.category }) {
                    Card(
                        modifier = Modifier
                            .padding(DP_5)
                            .clip(RoundedCornerShape(10.dp))
                            .combinedClickable(
                                onLongClick = {
                                    focusManager.clearFocus()
                                    showDeleteBtn.value = it.id
                                },
                                onClick = {
                                    if (showDeleteBtn.value == it.id) {
                                        showDeleteBtn.value = 0
                                    } else {
                                        showDeleteBtn.value = 0
                                        if (it.category.isNotEmpty()) {
                                            isErrorCategory.value = false
                                            category.value = it.category
                                            showCategoryPicker.value = false
                                            focusManager.clearFocus()
                                        }
                                    }
                                }
                            )
                    ) {
                        CategoryItem(
                            it = it,
                            showDeleteBtn = showDeleteBtn,
                            onDeleteCategory = onDeleteCategory,
                            listCategory = listCategory
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NewCategoryInputText(
    newCategory: MutableState<String>,
    isEditable: MutableState<Boolean>,
    type: MutableState<Int>,
    showDeleteBtn: MutableState<Int>,
    onAddNewCategory: (BillsItem) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    InputText(
        label = stringResource(R.string.type_new_category),
        value = newCategory,
        onValueChange = { newCategory.value = it.take(15) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        isEditable = isEditable,
        type = type,
        trailingIcon = {
            IconButton(onClick = {
                if (newCategory.value.isNotEmpty()) {
                    val bill = createBill(
                        typeCategory = if (type.value == TYPE_EXPENSES) TYPE_CATEGORY_EXPENSES else TYPE_CATEGORY_INCOME,
                        text = newCategory.value
                    )
                    onAddNewCategory.invoke(bill)
                    focusManager.clearFocus()
                    newCategory.value = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) showDeleteBtn.value = 0
            },
        onDone = {}
    )
}

@Composable
private fun CategoryItem(
    it: BillsItem,
    showDeleteBtn: MutableState<Int>,
    onDeleteCategory: (BillsItem) -> Unit,
    listCategory: MutableState<List<BillsItem>>
) {
    Row(
        modifier = Modifier
            .padding(DP_10)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = it.category,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        ImgBtn(
            modifier = Modifier.alpha(
                if (showDeleteBtn.value == it.id) 1f else 0f
            ),
            src = painterResource(R.drawable.ic_close),
            description = stringResource(R.string.b_delete_note),
            onClick = {
                if (showDeleteBtn.value == it.id) {
                    onDeleteCategory.invoke(it)
                    showDeleteBtn.value = 0
                    listCategory.value =
                        listCategory.value.filter { bill -> bill.id != it.id }
                }
            }
        )
    }
}

private fun createBill(typeCategory: Int, text: String) = BillsItem(
    0,
    type = typeCategory,
    "",
    "",
    "",
    category = text,
    "",
    "",
    "",
    false, "", "", "", "", ""
)