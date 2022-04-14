package com.billsAplication.presentation.addBill

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.AddBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.UpdateBillItemUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class AddBillViewModel @Inject constructor(
    private val addBill: AddBillItemUseCase,
    private val updateBill: UpdateBillItemUseCase,
    private val getAllDatabase: GetAllDataListUseCase
): ViewModel() {

    lateinit var list : LiveData<List<BillsItem>>

    fun getAll() {
        list =  getAllDatabase.invoke()
    }

    suspend fun add(billItem : BillsItem){
        addBill.invoke(billItem)
    }

    suspend fun update(billItem : BillsItem){
        updateBill.invoke(billItem)
    }


}