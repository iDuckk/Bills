package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.repository.BillsListRepository

class GetBillItemUsesCase(private val repo : BillsListRepository) {

    fun getItem(id : Int) {
        repo.getItem(id)
    }

}