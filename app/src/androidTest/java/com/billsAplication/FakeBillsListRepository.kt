package com.billsAplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class FakeBillsListRepository: BillsListRepository {

    val list = ArrayList<BillsItem>()

    val liveDada = MutableLiveData<List<BillsItem>>()

    private val item = BillsItem(
        id = 1,
        type = 1,
        month = "DECEMBER 2022",
        date = "10/12/2022",
        time = "10:00 AM",
        category = "Food",
        amount = "100.0",
        note = "Lenta",
        description = "",
        bookmark = false,
        image1 = "",
        image2 = "",
        image3 = "",
        image4 = "",
        image5 = "")

    init {
        list.add(item)
        list.add(item.copy(id = 2))
        list.add(item.copy(id = 3, type = 0))
    }

    override suspend fun updateItem(item: BillsItem) {
        list.forEachIndexed { index, billsItem ->
            if(billsItem.id == item.id) {
                list[index] = item
            }
        }
    }

    override suspend fun addItem(item: BillsItem) {
        list.add(item)
    }

    override fun getAllDataListLD(): LiveData<List<BillsItem>> {
        liveDada.postValue(list)
        return liveDada
    }

    override suspend fun deleteItem(item: BillsItem) {
        list.remove(item)
    }

    override suspend fun getItem(id: Int): BillsItem {
        return list.find { it.id == id }!!
    }

    override fun getMonthLD(month: String): LiveData<List<BillsItem>> {
        liveDada.postValue(list)
        return liveDada
    }

    override fun getMonthList(month: String): ArrayList<BillsItem> {
        TODO("Not yet implemented")
    }

    override fun getType(type: Int): LiveData<List<BillsItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTypeList(type: Int): List<BillsItem> {
        TODO("Not yet implemented")
    }

    override fun getBookmarks(type: Boolean): LiveData<List<BillsItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun checkPointDb(supportSQLiteQuery: SupportSQLiteQuery) {
        TODO("Not yet implemented")
    }

    override suspend fun closeDb() {
        TODO("Not yet implemented")
    }
}