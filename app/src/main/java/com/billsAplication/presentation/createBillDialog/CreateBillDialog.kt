package com.billsAplication.presentation.createBillDialog

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_EXPENSES
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_INCOME
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_EXPENSES
import com.billsAplication.extension.convertDateToMonthYear
import com.billsAplication.extension.convertTo12HourFormat
import com.billsAplication.extension.convertTo24HourFormat
import com.billsAplication.presentation.createBillDialog.createBill.AddBtnBlock
import com.billsAplication.presentation.createBillDialog.createBill.CategoryBlock
import com.billsAplication.presentation.createBillDialog.createBill.DateTimeBlock
import com.billsAplication.presentation.createBillDialog.createBill.InputTextBlock
import com.billsAplication.presentation.createBillDialog.createBill.TopBlock
import com.billsAplication.presentation.createBillDialog.createBill.TypeBtnBlock
import com.billsAplication.utils.LoadImageFromGallery
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_15
import com.billsAplication.utils.PagingConstants.DP_5
import java.io.ByteArrayOutputStream
import java.util.Base64


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
        val context = LocalContext.current

        val type = remember { mutableStateOf(bill?.type ?: TYPE_EXPENSES) }
        val isEditable = remember { mutableStateOf(bill == null) }
        val date = remember { mutableStateOf(bill?.date ?: "") }
        val time = remember { mutableStateOf(convertTo12HourFormat(bill?.time ?: "")) }
        val category = remember { mutableStateOf(bill?.category ?: "") }
        val newCategory = remember { mutableStateOf("") }
        val amount = remember { mutableStateOf(TextFieldValue(bill?.amount ?: "")) }
        val note = remember { mutableStateOf(bill?.note ?: "") }
        val description = remember { mutableStateOf(bill?.description ?: "") }
        var images by remember {
            mutableStateOf(
                bill?.let {
                    listOf(it.image1, it.image2, it.image3, it.image4, it.image5).filter { it.isNotEmpty() }
                } ?: emptyList()
            )
        }
        val showCategoryPicker = remember { mutableStateOf(false) }
        val bookmark = remember { mutableStateOf(false) }
        val showDatePicker = remember { mutableStateOf(false) }
        val showTimePicker = remember { mutableStateOf(false) }
        val showMainBlock = remember { mutableStateOf(bill != null) }
        val isErrorCategory = remember { mutableStateOf(false) }
        var previewImage by remember { mutableStateOf<ByteArray?>(null) }

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val application = context.applicationContext as Application
                val bitmap = LoadImageFromGallery(application).invoke(it)
                bitmap?.let { b ->
                    val widthPhoto = 600f
                    val heightPhoto = 800f
                    val matrix = Matrix()
                    matrix.postScale(widthPhoto / b.width, heightPhoto / b.height)
                    val resizedBitmap = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)

                    val stream = ByteArrayOutputStream()
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val base64Image = Base64.getEncoder().encodeToString(stream.toByteArray())

                    if (images.size < 5) {
                        images = images + base64Image
                    }
                }
            }
        }

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
                        IconButton(
                            onClick = {
                            if (images.size < 5) {
                                photoPickerLauncher.launch("image/*")
                            }
                        },
                            enabled = isEditable.value) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_attach_file),
                                contentDescription = stringResource(R.string.take_a_photo)
                            )
                        }
                        IconButton(
                            onClick = { /* Сделать фото с камеры */ },
                            enabled = isEditable.value) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_add_a_photo),
                                contentDescription = stringResource(R.string.take_a_photo)
                            )
                        }
                    }

                    LazyRow {
                        items(images.take(5)) { imageStr ->
                            val imageBytes = remember(imageStr) { Base64.getDecoder().decode(imageStr) }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(end = DP_5)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(imageBytes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(DP_5)
                                        .clickable { previewImage = imageBytes }
                                )
                                if (isEditable.value) {
                                    Text(
                                        text = stringResource(id = R.string.b_delete_note),
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        modifier = Modifier.clickable {
                                            images = images.filter { it != imageStr }
                                        }
                                    )
                                }
                            }
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

        previewImage?.let { byteArray ->
            Dialog(
                onDismissRequest = { previewImage = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { previewImage = null },
                    contentAlignment = Alignment.Center
                ) {
                    // Размытый фон
                    Image(
                        painter = rememberAsyncImagePainter(byteArray),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(30.dp),
                        contentScale = ContentScale.Crop
                    )
                    // Затемняющий слой для глубины
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    // Основное фото
                    Image(
                        painter = rememberAsyncImagePainter(byteArray),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
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
