package com.billsAplication.data.room.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.data.room.billsDb.BillDao
import com.billsAplication.data.room.mapper.BillMapper
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class BillsListRepositoryImpl @Inject constructor(private val billDao: BillDao, private val mapper: BillMapper) : BillsListRepository {

    override suspend fun addItem(item: BillsItem) {
        billDao.insert(mapper.mapBillItemToBillEntity(item))
    }

    override suspend fun deleteItem(item : BillsItem) {
        billDao.delete(mapper.mapBillItemToBillEntity(item))
    }

    override fun getAllDataList(): LiveData<List<BillsItem>> {
        return Transformations.map(billDao.getAll()){
            it.map {
                mapper.mapBillEntityToBillItem(it)
            }
        }
    }

    override suspend fun getItem(id: Int) : BillsItem {
        return mapper.mapBillEntityToBillItem(billDao.getItem(id))
    }

    override fun getMonthList(month : String): LiveData<List<BillsItem>> {
        return Transformations.map(billDao.getMonth(month)){
            it.map{
                mapper.mapBillEntityToBillItem(it)
            }
        }
    }

    override fun getType(type: Int): LiveData<List<BillsItem>> {
        return Transformations.map(billDao.getType(type)){
            it.map{
                mapper.mapBillEntityToBillItem(it)
            }
        }
    }

    override fun getBookmarks(type: Boolean): LiveData<List<BillsItem>> {
        return Transformations.map(billDao.getBookmarks(type)){
            it.map{
                mapper.mapBillEntityToBillItem(it)
            }
        }
    }

    override suspend fun updateItem(item: BillsItem) {
        billDao.update(mapper.mapBillItemToBillEntity(item))
    }

    override suspend fun checkPointDb(supportSQLiteQuery: SupportSQLiteQuery){
        billDao.checkpoint(supportSQLiteQuery)
    }
}