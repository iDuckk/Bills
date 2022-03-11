package com.billsAplication.domain.billsUseCases

import androidx.lifecycle.LiveData
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class GetMonthListUseCase(private val repo : BillsListRepository) {

    fun getMonthList(month : String) : LiveData<List<BillsItem>> {
        return repo.getMonthList(month)
    }

}