package com.billsAplication.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.domain.billsUseCases.CheckPointDb
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val checkPoint: CheckPointDb
): ViewModel()  {

    suspend fun chekPoint(supportSQLiteQuery: SupportSQLiteQuery){
        checkPoint.invoke(supportSQLiteQuery)
    }
}