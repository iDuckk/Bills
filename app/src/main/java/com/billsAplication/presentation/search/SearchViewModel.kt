package com.billsAplication.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val getAllDatabase: GetAllDataListUseCase,
    private val delete : DeleteBillItemUseCase
): ViewModel() {

    lateinit var list : LiveData<List<BillsItem>>

    init {
        getAll()
    }

    fun getAll() {
        list =  getAllDatabase.invoke()
    }

    suspend fun delete(item: BillsItem){
        delete.invoke(item)
    }

}