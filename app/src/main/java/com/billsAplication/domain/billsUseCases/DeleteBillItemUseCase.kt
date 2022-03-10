package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.repository.BillsListRepository

class DeleteBillItemUseCase(private val repo : BillsListRepository) {

    fun deleteItem(id : Int) {
        repo.deleteItem(id)
    }

}