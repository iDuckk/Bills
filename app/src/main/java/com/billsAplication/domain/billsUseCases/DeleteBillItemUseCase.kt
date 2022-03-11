package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class DeleteBillItemUseCase(private val repo : BillsListRepository) {

    suspend fun deleteItem(item : BillsItem) {
        repo.deleteItem(item)
    }

}