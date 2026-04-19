package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchBillsUseCase @Inject constructor(private val repo: BillsListRepository) {
    operator fun invoke(
        note: String,
        category: String,
        minAmount: Double,
        maxAmount: Double,
        startDate: String,
        endDate: String
    ): Flow<List<BillsItem>> = repo.searchBills(note, category, minAmount, maxAmount, startDate, endDate)
}
