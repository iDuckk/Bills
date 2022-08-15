package com.billsAplication.domain.billsUseCases

import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class CheckPointDb@Inject constructor(private val repo : BillsListRepository) {
    suspend operator fun invoke(supportSQLiteQuery: SupportSQLiteQuery) = repo.checkPointDb(supportSQLiteQuery)
}