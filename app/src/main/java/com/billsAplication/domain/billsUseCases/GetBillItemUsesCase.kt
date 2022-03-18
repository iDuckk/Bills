package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetBillItemUsesCase @Inject constructor(private val repo : BillsListRepository) {

    suspend operator fun invoke(id : Int) = repo.getItem(id)

}