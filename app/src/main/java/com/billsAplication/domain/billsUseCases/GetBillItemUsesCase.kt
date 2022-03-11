package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class GetBillItemUsesCase(private val repo : BillsListRepository) {

    suspend operator fun invoke(id : Int) = repo.getItem(id)

}