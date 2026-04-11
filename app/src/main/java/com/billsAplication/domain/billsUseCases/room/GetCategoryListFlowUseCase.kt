package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.model.CategoryBillItem
import com.billsAplication.domain.repository.BillsListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryListFlowUseCase @Inject constructor(private val repo: BillsListRepository) {
    operator fun invoke(type: Int): Flow<List<CategoryBillItem>> = repo.getCategoryListFlow(type)
}
