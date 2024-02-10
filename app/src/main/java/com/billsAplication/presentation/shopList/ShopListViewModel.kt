package com.billsAplication.presentation.shopList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsAplication.domain.billsUseCases.room.AddNoteUseCase
import com.billsAplication.domain.billsUseCases.room.DeleteNoteUseCase
import com.billsAplication.domain.billsUseCases.room.GetNotesListUseCase
import com.billsAplication.domain.model.NoteItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.TimeZone
import javax.inject.Inject

class ShopListViewModel @Inject constructor(
    private val getNotesListUseCase: GetNotesListUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    fun notes() = getNotesListUseCase.invoke()

    fun addNote(item: NoteItem) {
        viewModelScope.launch(Dispatchers.IO) {
            addNoteUseCase.invoke(item = item)
        }
    }

    fun deleteNote(item: NoteItem) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNoteUseCase.invoke(item = item)
        }
    }
}