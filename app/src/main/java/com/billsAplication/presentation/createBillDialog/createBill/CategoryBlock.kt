package com.billsAplication.presentation.createBillDialog.createBill

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_EXPENSES
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_INCOME
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_EXPENSES
import com.billsAplication.domain.model.CategoryBillItem
import com.billsAplication.presentation.components.ImgBtn
import com.billsAplication.presentation.components.InputText
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_5

@Composable
fun CategoryBlock(
    isErrorCategory: MutableState<Boolean>,
    listCategory: List<CategoryBillItem>,
    category: MutableState<String>,
    newCategory: MutableState<String>,
    isEditable: Boolean,
    type: Int,
    showCategoryPicker: MutableState<Boolean>,
    onAddNewCategory: (CategoryBillItem) -> Unit,
    onDeleteCategory: (CategoryBillItem) -> Unit
) {
    // Category
    InputText(
        label = stringResource(R.string.title_category),
        value = category.value,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        isEditable = isEditable,
        readOnly = true,
        type = type,
        isError = isErrorCategory,
        trailingIcon = {
            if (isErrorCategory.value)
                Icon(
                    Icons.Filled.Info,
                    stringResource(R.string.toast_fill_category),
                    tint = MaterialTheme.colorScheme.error
                )
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) showCategoryPicker.value = true
            },
        onValueChange = { category.value = it },
        onDone = {}
    )
    if (isErrorCategory.value) {
        Text(
            text = stringResource(R.string.toast_fill_category),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = DP_10)
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
    isEditable: Boolean,
    type: Int,
    listCategory: List<CategoryBillItem>,
    category: MutableState<String>,
    showCategoryPicker: MutableState<Boolean>,
    isErrorCategory: MutableState<Boolean>,
    onAddNewCategory: (CategoryBillItem) -> Unit,
    onDeleteCategory: (CategoryBillItem) -> Unit
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(
                    items = listCategory.sortedBy { it.category },
                    key = { it.id }
                ) { item ->
                    Card(
                        modifier = Modifier
                            .padding(DP_5)
                            .clip(RoundedCornerShape(10.dp))
                            .combinedClickable(
                                onLongClick = {
                                    focusManager.clearFocus()
                                    showDeleteBtn.value = item.id
                                },
                                onClick = {
                                    if (showDeleteBtn.value == item.id) {
                                        showDeleteBtn.value = 0
                                    } else {
                                        showDeleteBtn.value = 0
                                        if (item.category.isNotEmpty()) {
                                            isErrorCategory.value = false
                                            category.value = item.category
                                            showCategoryPicker.value = false
                                            focusManager.clearFocus()
                                        }
                                    }
                                }
                            )
                    ) {
                        CategoryItem(
                            it = item,
                            showDeleteBtn = showDeleteBtn,
                            onDeleteCategory = onDeleteCategory
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
    isEditable: Boolean,
    type: Int,
    showDeleteBtn: MutableState<Int>,
    onAddNewCategory: (CategoryBillItem) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    InputText(
        label = stringResource(R.string.type_new_category),
        value = newCategory.value,
        onValueChange = { newCategory.value = it.take(15) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        isEditable = isEditable,
        type = type,
        trailingIcon = {
            IconButton(onClick = {
                if (newCategory.value.isNotEmpty()) {
                    val categoryItem = CategoryBillItem(
                        id = 0,
                        typeCategory = if (type == TYPE_EXPENSES) TYPE_CATEGORY_EXPENSES else TYPE_CATEGORY_INCOME,
                        category = newCategory.value
                    )
                    onAddNewCategory.invoke(categoryItem)
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
    it: CategoryBillItem,
    showDeleteBtn: MutableState<Int>,
    onDeleteCategory: (CategoryBillItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = it.category,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        if (showDeleteBtn.value == it.id) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImgBtn(
                    modifier = Modifier.size(20.dp).padding(end = 4.dp),
                    src = painterResource(id = R.drawable.ic_close),
                    description = stringResource(R.string.search_cancel),
                ) {
                    onDeleteCategory.invoke(it)
                    showDeleteBtn.value = 0
                }
                ImgBtn(
                    modifier = Modifier.size(22.dp).alpha(0f),
                    src = painterResource(id = R.drawable.ic_close),
                    description = stringResource(R.string.b_delete_note),
                ) {
                    showDeleteBtn.value = 0
                }
            }
        }
    }
}
