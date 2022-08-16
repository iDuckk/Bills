package com.billsAplication.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.domain.billsUseCases.CheckPointDb
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val checkPoint: CheckPointDb
): ViewModel()  {

    fun chekPoint(supportSQLiteQuery: SupportSQLiteQuery){
        viewModelScope.launch{
            checkPoint.invoke(supportSQLiteQuery)
        }
    }
}