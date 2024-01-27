package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class SummaryAmountUseCase @Inject constructor(private val repo : BillsListRepository) {
    operator fun invoke(month: String, type: Int)
        = repo.summaryAmount(month = month, type = type)
}