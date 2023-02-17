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
    suspend fun getTypeList(type : Int): List<BillEntity>

    @Query("SELECT * FROM bills_list WHERE bookmark = :type")
    fun getBookmarks(type : Boolean): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE id = :id")
    suspend fun getItem(id : Int): BillEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item : BillEntity)

    @Delete
    suspend fun delete(item : BillEntity)

    @Update
    suspend fun update(item : BillEntity)

    @RawQuery
    suspend fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int

}