package com.billsAplication.presentation.fragmentDialogCategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.AddBillItemUseCase
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetTypeUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class FragmentDialogCategoryViewModel @Inject constructor(
    private val getTypeUseCase: GetTypeUseCase,
    private val addBillItemUseCase: AddBillItemUseCase,
    private val deleteBillItemUseCase: DeleteBillItemUseCase
) : ViewModel() {

    private val TYPE_CATEGORY = 2

    lateinit var list : LiveData<List<BillsItem>>

    fun getCategoryType() {
        list =  getTypeUseCase.invoke(TYPE_CATEGORY)
    }

    suspend fun deleteCategory(billItem : BillsItem){
        deleteBillItemUseCase.invoke(billItem)
    }

    suspend fun addCategory(billItem : BillsItem){
        addBillItemUseCase.invoke(billItem)
    }

}