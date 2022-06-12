package com.billsAplication.presentation.shopList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetBookmarksUseCase
import com.billsAplication.domain.billsUseCases.GetTypeUseCase
import com.billsAplication.domain.billsUseCases.UpdateBillItemUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class NoteViewModel @Inject constructor(
    private val update: UpdateBillItemUseCase,
    private val delete : DeleteBillItemUseCase,
    private val getTypeList: GetTypeUseCase
) : ViewModel() {

    private val TYPE_NOTE = 3

    lateinit var list : LiveData<List<BillsItem>>

    init {
        getNoteList()
    }

    fun getNoteList() {
        list =  getTypeList.invoke(TYPE_NOTE)
    }

    suspend fun updateNotesList(billItem : BillsItem){
        update.invoke(billItem)
    }

    suspend fun delete(item: BillsItem){
        delete.invoke(item)
    }

}