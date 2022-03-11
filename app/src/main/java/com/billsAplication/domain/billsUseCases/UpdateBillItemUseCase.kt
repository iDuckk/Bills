package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class UpdateBillItemUseCase(private val repo : BillsListRepository) {

    suspend operator fun invoke(item : BillsItem) = repo.updateItem(item)

}