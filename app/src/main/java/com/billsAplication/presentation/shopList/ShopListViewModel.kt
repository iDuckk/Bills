package com.billsAplication.presentation.shopList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.*
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class ShopListViewModel @Inject constructor(
    private val addBill: AddBillItemUseCase,
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

    suspend fun add(textNote: String, color: String){
        addBill.invoke(newItem(0, textNote, color))
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