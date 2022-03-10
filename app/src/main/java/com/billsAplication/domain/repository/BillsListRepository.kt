package com.billsAplication.domain.repository

import com.billsAplication.domain.model.BillsItem

interface BillsListRepository {

    fun addItem(item : BillsItem)

    fun deleteItem(id : Int)

    fun getAllDataList() : List<BillsItem>

    fun getItem(id : Int)

    fun getMonthList() : List<BillsItem>

    fun updateItem(item : BillsItem)

}