package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class GetMonthListUseCase(private val repo : BillsListRepository) {

    fun getMonthList() : List<BillsItem> {
        return repo.getMonthList()
    }

}