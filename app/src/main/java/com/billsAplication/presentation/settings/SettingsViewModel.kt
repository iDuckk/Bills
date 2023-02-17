package com.billsAplication.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.domain.billsUseCases.CheckPointDb
import com.billsAplication.domain.billsUseCases.CloseDb
import com.billsAplication.domain.billsUseCases.GetAllDataListLDUseCase
import com.billsAplication.domain.model.BillsItem
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val checkPoint: CheckPointDb,
    private val closeDb: CloseDb,
    private val getAllDatabase : GetAllDataListLDUseCase
    ): ViewModel()  {

    lateinit var listAll: LiveData<List<BillsItem>>

    fun chekPoint(supportSQLiteQuery: SupportSQLiteQuery){
        viewModelScope.launch{
            checkPoint.invoke(supportSQLiteQuery)
        }
    }

    fun closeDb(){
        viewModelScope.launch{
            closeDb.invoke()
        }
    }

    fun getAll(){
        listAll = getAllDatabase.invoke()
    }

}