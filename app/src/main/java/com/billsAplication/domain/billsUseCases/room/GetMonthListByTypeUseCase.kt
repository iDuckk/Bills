package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetMonthListByTypeUseCase @Inject constructor(private val repo : BillsListRepository) {
    operator fun invoke(month: String, type: Int)
        = repo.getMonthListByType(month = month, type = type)
}