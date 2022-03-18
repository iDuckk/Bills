package com.billsAplication.domain.billsUseCases

import androidx.lifecycle.LiveData
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetMonthListUseCase @Inject constructor(private val repo : BillsListRepository) {

    operator fun invoke(month : String) = repo.getMonthList(month)

}