package com.billsAplication.presentation.shopList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsAplication.domain.billsUseCases.room.AddNoteUseCase
import com.billsAplication.domain.billsUseCases.room.GetNotesListUseCase
import com.billsAplication.domain.model.NoteItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.TimeZone
import javax.inject.Inject

class ShopListViewModel @Inject constructor(
    private val getNotesListUseCase: GetNotesListUseCase,
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    fun notes() = getNotesListUseCase.invoke()

    fun addNote(textNote: String, color: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addNoteUseCase.invoke(
                item = NoteItem(
                    dateTimeCreation = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                        .toString(),
                    textNote = textNote,
                    color = color
                )
            )
        }
    }
}