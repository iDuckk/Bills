package com.billsAplication.domain.billsUseCases

import androidx.lifecycle.LiveData
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class GetAllDataListUseCase(private val repo : BillsListRepository) {

    fun getAllDataList() : LiveData<List<BillsItem>> {
        return repo.getAllDataList()
    }

}