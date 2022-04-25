package com.billsAplication.data.room.billsDb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.billsAplication.data.room.model.BillEntity
import com.billsAplication.domain.model.BillsItem

@Dao
interface BillDao {

    @Query("SELECT * FROM bills_list")
    fun getAll(): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE month = :month")
    fun getMonth(month : String): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE type = :type")
    fun getType(type : Int): LiveData<List<BillEntity>>

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

}