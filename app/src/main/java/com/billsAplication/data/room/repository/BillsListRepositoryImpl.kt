package com.billsAplication.data.room.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.data.room.billsDb.BillDao
import com.billsAplication.data.room.billsDb.BillDatabase
import com.billsAplication.data.room.mapper.BillMapper
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.NoteItem
import com.billsAplication.domain.repository.BillsListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BillsListRepositoryImpl @Inject constructor(private val billDao: BillDao, private val mapper: BillMapper) : BillsListRepository {
    override fun summaryAmount(month: String, type: Int) =
        billDao.summaryAmount(month = month, type = type)

    override fun getMonthListByType(month: String, type: Int): List<BillsItem> {
        return billDao.getMonthListByType(month = month, type = type).map {
            mapper.mapBillEntityToBillItem(item = it)
        }
    }

    override fun getMonthListByTypeCategory(
        month: String,
        type: Int,
        category: String
    ): List<BillsItem> {
        return billDao.getMonthListByTypeCategory(month = month, type = type, category = category).map {
            mapper.mapBillEntityToBillItem(item = it)
        }
    }

    override fun getAllDataList(): List<BillsItem> {
        var newList = ArrayList<BillsItem>()
        val list = billDao.getAll()
        list.forEach {
            newList.add(mapper.mapBillEntityToBillItem(item = it))
        }
        return newList
    }

    override suspend fun checkPointDb(supportSQLiteQuery: SupportSQLiteQuery){
        billDao.checkpoint(supportSQLiteQuery = supportSQLiteQuery)
    }

    override suspend fun closeDb(){
        BillDatabase.destroyInstance()
    }

    override fun getNotesList(): Flow<List<NoteItem>> = billDao.getNotesFlow().map {
        mapper.mapNoteEntityToNoteItemList(list = it)
    }

    override fun addNote(item: NoteItem) {
        billDao.insertNote(mapper.mapNoteItemToNoteEntity(item = item))
    }

    /**
     * Old repo
     * */

    override suspend fun addItem(item: BillsItem) {
        billDao.insert(mapper.mapBillItemToBillEntity(item))
    }

    override suspend fun deleteItem(item : BillsItem) {
        billDao.delete(mapper.mapBillItemToBillEntity(item))
    }

    override suspend fun getItem(id: Int) : BillsItem {
        return mapper.mapBillEntityToBillItem(billDao.getItem(id))
    }

    @SuppressLint("CheckResult")
    override fun getMonthLD(month : String): LiveData<List<BillsItem>> {
        return billDao.getMonthLD(month).map {
            mapper.mapBillEntityToBillItemList(it)
        }
    }

    override fun getMonthList(month: String) = mapper.mapBillEntityToBillItemList(billDao.getMonthList(month))

    @SuppressLint("CheckResult")
    override fun getType(type: Int): LiveData<List<BillsItem>> {
        return billDao.getType(type).map {
            mapper.mapBillEntityToBillItemList(it)
        }
    }

    override suspend fun getTypeList(type: Int): List<BillsItem> {
        var newList = ArrayList<BillsItem>()
        val list = billDao.getTypeList(type)
        list.forEach {
            newList.add(mapper.mapBillEntityToBillItem(it))
        }
        return newList
    }

    @SuppressLint("CheckResult")
    override fun getBookmarks(type: Boolean): LiveData<List<BillsItem>> {
        return billDao.getBookmarks(type).map {
            mapper.mapBillEntityToBillItemList(it)
        }
    }

    override suspend fun updateItem(item: BillsItem) {
        billDao.update(mapper.mapBillItemToBillEntity(item))
    }

    @SuppressLint("CheckResult")
    override fun getAllDataListLD(): LiveData<List<BillsItem>> {
        return billDao.getAllLD().map {
            mapper.mapBillEntityToBillItemList(it)
        }
    }
}