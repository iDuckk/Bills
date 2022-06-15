package com.billsAplication.presentation.addNote

import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.AddBillItemUseCase
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetBillItemUsesCase
import com.billsAplication.domain.billsUseCases.UpdateBillItemUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class AddNoteViewModel@Inject constructor(
    private val addBill: AddBillItemUseCase,
    private val update: UpdateBillItemUseCase,
    private val delete : DeleteBillItemUseCase,
    private val getBill: GetBillItemUsesCase) : ViewModel() {

    private val TYPE_NOTE = 3

    suspend fun getItem(id : Int): BillsItem{
        return getBill.invoke(id)
    }

    suspend fun add(note : String, color: String){
        addBill.invoke(newItem(0, note, color))
    }

    suspend fun updateNotesList(billItem : BillsItem){
        update.invoke(billItem)
    }

    suspend fun delete(billItem : BillsItem){
        delete.invoke(billItem)
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