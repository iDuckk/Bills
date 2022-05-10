package com.billsAplication.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val getAllDatabase: GetAllDataListUseCase
): ViewModel() {

    lateinit var list : LiveData<List<BillsItem>>

    init {
        getAll()
    }

    fun getAll() {
        list =  getAllDatabase.invoke()
    }

}