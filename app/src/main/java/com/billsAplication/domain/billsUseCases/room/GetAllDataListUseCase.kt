package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetAllDataListUseCase @Inject constructor(private val repo : BillsListRepository) {
    operator fun invoke() = repo.getAllDataList()

}