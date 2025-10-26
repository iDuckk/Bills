package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetCategoryListUseCase @Inject constructor(private val repo : BillsListRepository) {

    operator fun invoke(type : Int) = repo.getCategoryList(type)

}