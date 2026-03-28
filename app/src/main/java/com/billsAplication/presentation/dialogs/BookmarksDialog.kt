package com.billsAplication.presentation.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.bookmarks.view.BookmarkItem
import com.billsAplication.presentation.components.ImgBtn
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_15

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksDialog(
    showDialog: MutableState<Boolean>,
    bookmarks: List<BillsItem>,
    onBookmarkClick: (BillsItem) -> Unit,
    onDeleteBookmarks: (List<BillsItem>) -> Unit,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val selectedItems = remember { mutableStateListOf<BillsItem>() }

    if (showDialog.value) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = {
                showDialog.value = false
                onDismiss()
            },
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(horizontal = DP_15)
                    .windowInsetsPadding(WindowInsets.ime)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.title_bookmarks),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    if (selectedItems.isNotEmpty()) {
                        ImgBtn(
                            src = painterResource(id = R.drawable.ic_delete_forever),
                            description = "Delete"
                        ) {
                            onDeleteBookmarks(selectedItems.toList())
                            selectedItems.clear()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DP_10))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(bookmarks) { item ->
                        BookmarkItem(
                            item = item,
                            isSelected = selectedItems.contains(item),
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    if (selectedItems.contains(item)) {
                                        selectedItems.remove(item)
                                    } else {
                                        selectedItems.add(item)
                                    }
                                } else {
                                    onBookmarkClick(item)
                                }
                            },
                            onLongClick = {
                                if (selectedItems.contains(item)) {
                                    selectedItems.remove(item)
                                } else {
                                    selectedItems.add(item)
                                }
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(DP_15))
            }
        }
    }
}
