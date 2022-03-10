package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class GetAllDataListUseCase(private val repo : BillsListRepository) {

    fun getAllDataList() : List<BillsItem> {
        return repo.getAllDataList()
    }

}