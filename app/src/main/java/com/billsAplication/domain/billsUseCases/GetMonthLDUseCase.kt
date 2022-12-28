package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetMonthLDUseCase @Inject constructor(private val repo : BillsListRepository) {

    operator fun invoke(month : String) = repo.getMonthLD(month)

}