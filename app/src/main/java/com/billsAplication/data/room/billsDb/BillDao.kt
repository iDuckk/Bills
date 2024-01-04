package com.billsAplication.data.room.billsDb

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.data.room.model.BillEntity


@Dao
interface BillDao {

    @Query("SELECT * FROM bills_list")
    fun getAllLD(): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list")
    fun getAll(): List<BillEntity>

    @Query("SELECT * FROM bills_list WHERE month = :month")
    fun getMonthLD(month : String): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE month = :month")
    fun getMonthList(month : String): List<BillEntity>

    @Query("SELECT * FROM bills_list WHERE type = :type")
    fun getType(type : Int): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE type = :type")
    fun getTypeList(type : Int): List<BillEntity>

    @Query("SELECT * FROM bills_list WHERE bookmark = :type")
    fun getBookmarks(type : Boolean): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE id = :id")
    fun getItem(id : Int): BillEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item : BillEntity)

    @Delete
    fun delete(item : BillEntity)

    @Update
    fun update(item : BillEntity)

    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int

}