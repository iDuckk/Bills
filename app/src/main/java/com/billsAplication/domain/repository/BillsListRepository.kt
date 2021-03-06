package com.billsAplication.domain.repository

import androidx.lifecycle.LiveData
import com.billsAplication.domain.model.BillsItem

interface BillsListRepository {

    suspend fun addItem(item : BillsItem)

    suspend fun deleteItem(item : BillsItem)

    fun getAllDataList() : LiveData<List<BillsItem>>

    suspend fun getItem(id : Int) : BillsItem

    fun getMonthList(month : String) : LiveData<List<BillsItem>>

    suspend fun updateItem(item : BillsItem)

    fun getType(type : Int) : LiveData<List<BillsItem>>

    fun getBookmarks(type : Boolean) : LiveData<List<BillsItem>>

}