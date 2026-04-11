package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.model.CategoryBillItem
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(private val repo: BillsListRepository) {
    operator fun invoke(item: CategoryBillItem) = repo.addCategory(item)
}
