package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetMonthListByTypeCategoryUseCase @Inject constructor(private val repo : BillsListRepository) {
    operator fun invoke(month: String, type: Int, category: String)
        = repo.getMonthListByTypeCategory(month = month, type = type, category = category)
}