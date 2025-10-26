package com.billsAplication.presentation.fragmentDialogCategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.AddBillItemUseCase
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetCategoryListUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class FragmentDialogCategoryViewModel @Inject constructor(
    private val getCategoryListUseCase: GetCategoryListUseCase,
    private val addBillItemUseCase: AddBillItemUseCase,
    private val deleteBillItemUseCase: DeleteBillItemUseCase
) : ViewModel() {

    lateinit var list : MutableLiveData<List<BillsItem>>

    fun getCategoryType(type: Int) {
        list.postValue(getCategoryListUseCase.invoke(type))
    }

    suspend fun deleteCategory(billItem : BillsItem){
        deleteBillItemUseCase.invoke(billItem)
    }

    suspend fun addCategory(billItem : BillsItem){
        addBillItemUseCase.invoke(billItem)
    }

}