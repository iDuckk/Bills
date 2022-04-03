package com.billsAplication.presentation.addBill

import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.AddBillItemUseCase
import com.billsAplication.domain.billsUseCases.UpdateBillItemUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class AddBillViewModel @Inject constructor(
    private val addBill: AddBillItemUseCase,
    private val updateBill: UpdateBillItemUseCase
): ViewModel() {

    suspend fun add(billItem : BillsItem){
        addBill.invoke(billItem)
    }

    suspend fun update(billItem : BillsItem){
        updateBill.invoke(billItem)
    }


}