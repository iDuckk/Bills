package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class AddBillItemUseCase(private val repo : BillsListRepository) {

    suspend operator fun invoke(item  : BillsItem) = repo.addItem(item)

}