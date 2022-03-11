package com.billsAplication.data.room.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.billsAplication.data.room.billsDb.BillDatabase
import com.billsAplication.data.room.mapper.BillMapper
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository

class BillsListRepositoryImpl(private val application: Application) : BillsListRepository {

    private val billDao = BillDatabase.getDatabase(application).billDao()

    private val mapper = BillMapper()

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

    override suspend fun updateItem(item: BillsItem) {
        billDao.update(mapper.mapBillItemToBillEntity(item))
    }
}