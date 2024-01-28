package com.billsAplication.presentation.shopList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsAplication.domain.billsUseCases.*
import com.billsAplication.domain.billsUseCases.room.AddNoteUseCase
import com.billsAplication.domain.billsUseCases.room.GetNotesListUseCase
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.NoteItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.TimeZone
import javax.inject.Inject

class ShopListViewModel @Inject constructor(
    private val getNotesListUseCase: GetNotesListUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val addBill: AddBillItemUseCase,
    private val getTypeList: GetTypeUseCase
) : ViewModel() {

    lateinit var _listNotes: MutableLiveData<List<NoteItem>>
    val listNotes: LiveData<List<NoteItem>>
        get() = _listNotes


    private val TYPE_NOTE = 3

    lateinit var list : LiveData<List<BillsItem>>

    init {
        getNoteList()
    }

    fun getNoteList() {
        list =  getTypeList.invoke(TYPE_NOTE)
    }

    suspend fun add(textNote: String, color: String){
        addBill.invoke(newItem(0, textNote, color))
    }

    fun addNote(textNote: String, color: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addNoteUseCase.invoke(item = NoteItem(
                dateTimeCreation = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId()).toString(),
                textNote = textNote,
                color = color
            ))
        }
    }

    fun notesList() {
        viewModelScope.launch(Dispatchers.IO) {
            getNotesListUseCase.invoke().forEach {
                Log.d("TAG", "notesList: ${it.textNote}")
            }
        }
    }

    fun newItem(id: Int, textNote: String, color: String): BillsItem {
        return BillsItem(
            id,
            TYPE_NOTE,
            "",
            "",
            "",
            "",
            "",
            textNote,
            color,
            false, "", "", "", "", ""
        )
    }

}