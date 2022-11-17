package com.billsAplication.domain.billsUseCases

import androidx.lifecycle.LiveData
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetTypeListUseCase @Inject constructor(private val repo : BillsListRepository) {

    suspend operator fun invoke(type : Int) = repo.getTypeList(type)

}