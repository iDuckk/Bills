package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class UpdateBillItemUseCase @Inject constructor(private val repo : BillsListRepository) {

    suspend operator fun invoke(item : BillsItem) = repo.updateItem(item)

}