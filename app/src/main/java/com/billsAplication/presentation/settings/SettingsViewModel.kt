package com.billsAplication.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.domain.billsUseCases.room.CheckPointDb
import com.billsAplication.domain.billsUseCases.room.CloseDb
import com.billsAplication.domain.billsUseCases.room.GetAllDataListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val checkPoint: CheckPointDb,
    private val closeDb: CloseDb,
    private val getAllDatabase : GetAllDataListUseCase
    ): ViewModel()  {

    fun checkPoint(supportSQLiteQuery: SupportSQLiteQuery) {
        viewModelScope.launch(Dispatchers.IO) {
            checkPoint.invoke(supportSQLiteQuery)
        }
    }

    fun closeDb() {
        viewModelScope.launch(Dispatchers.IO) {
            closeDb.invoke()
        }
    }

    fun getAll() = getAllDatabase.invoke()

}