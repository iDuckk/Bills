package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class GetBillItemUsesCase(private val repo : BillsListRepository) {

    suspend fun getItem(id : Int) : BillsItem {
        return repo.getItem(id)
    }

}