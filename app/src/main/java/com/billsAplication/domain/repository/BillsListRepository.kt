package com.billsAplication.domain.repository

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.domain.model.BillsItem

interface BillsListRepository {

    fun summaryAmount(month: String, type: Int): Double

    fun getMonthListByType(month: String, type: Int): List<BillsItem>

    fun getMonthListByTypeCategory(month: String, type: Int, category: String): List<BillsItem>

    suspend fun checkPointDb(supportSQLiteQuery: SupportSQLiteQuery)

    suspend fun closeDb()

    /**
     * Old repo
     * */

    suspend fun addItem(item : BillsItem)

    suspend fun deleteItem(item : BillsItem)

    fun getAllDataList() : List<BillsItem>

    suspend fun getItem(id : Int) : BillsItem

    fun getMonthLD(month : String) : LiveData<List<BillsItem>>

    fun getMonthList(month : String) : ArrayList<BillsItem>

    suspend fun updateItem(item : BillsItem)

    fun getType(type : Int) : LiveData<List<BillsItem>>

    suspend fun getTypeList(type : Int) : List<BillsItem>

    fun getBookmarks(type : Boolean) : LiveData<List<BillsItem>>

    /**
     * Кандидат на удаление
     * */

    fun getAllDataListLD() : LiveData<List<BillsItem>>

}