package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetBillsListFlowUseCase @Inject constructor(private val repo : BillsListRepository) {
    operator fun invoke(month: String) = repo.getMonthListFlow(month = month)
}