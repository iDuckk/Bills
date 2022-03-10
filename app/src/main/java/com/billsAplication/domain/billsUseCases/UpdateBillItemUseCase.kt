package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class UpdateBillItemUseCase(private val repo : BillsListRepository) {

    fun updateItem(item : BillsItem) {
        repo.updateItem(item)
    }

}