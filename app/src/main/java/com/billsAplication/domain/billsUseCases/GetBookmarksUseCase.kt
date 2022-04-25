package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(private val repo : BillsListRepository) {

    operator fun invoke(type : Boolean) = repo.getBookmarks(type)

}