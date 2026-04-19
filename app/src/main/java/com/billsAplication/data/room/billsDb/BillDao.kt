package com.billsAplication.data.room.billsDb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.billsAplication.data.room.model.BillEntity
import com.billsAplication.data.room.model.CategoryBillEntity
import com.billsAplication.data.room.model.NoteEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface BillDao {

    @Query("SELECT SUM(CAST(REPLACE(amount, ',', '') AS REAL)) FROM bills_list WHERE month = :month AND type = :type AND bookmark = :bookmark")
    fun summaryAmount(month : String, type : Int, bookmark : Boolean = false): Double

    @Query("SELECT * FROM bills_list WHERE month = :month AND type = :type AND bookmark = :bookmark")
    fun getMonthListByType(month : String, type : Int, bookmark : Boolean = false): List<BillEntity>

    @Query("SELECT * FROM bills_list WHERE month = :month AND type = :type AND category = :category AND bookmark = :bookmark")
    fun getMonthListByTypeCategory(month : String, type : Int, category : String, bookmark : Boolean = false): List<BillEntity>

    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int

    @Query("SELECT * FROM notes_list")
    fun getNotesFlow(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(item : NoteEntity)

    @Delete
    fun deleteNote(item : NoteEntity)

    @Query("SELECT * FROM bills_list WHERE  bookmark = :bookmark")
    fun getAll(bookmark : Boolean = false): List<BillEntity>

    @Query("SELECT * FROM bills_list WHERE bookmark = :bookmark AND month = :month ORDER BY date, time")
    fun getMonthListFlow(month : String, bookmark : Boolean = false): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE type = :type AND bookmark = :bookmark")
    fun getCategoryList(type : Int, bookmark : Boolean = false): List<BillEntity>

    @Query("SELECT DISTINCT note FROM bills_list WHERE note != ''")
    fun getUniqueNotes(): List<String>

    @Query("SELECT * FROM categories_list WHERE typeCategory = :type")
    fun getCategoryListFlow(type: Int): Flow<List<CategoryBillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(item: CategoryBillEntity)

    @Delete
    fun deleteCategory(item: CategoryBillEntity)

    @Query("""
        SELECT * FROM bills_list 
        WHERE (:note = '' OR note LIKE '%' || :note || '%') 
        AND (:category = '' OR category = :category) 
        AND (CAST(REPLACE(amount, ',', '') AS REAL) BETWEEN :minAmount AND :maxAmount) 
        AND (substr(date, 7, 4) || substr(date, 4, 2) || substr(date, 1, 2) BETWEEN :startDate AND :endDate) 
        ORDER BY substr(date, 7, 4) DESC, substr(date, 4, 2) DESC, substr(date, 1, 2) DESC, time DESC
    """)
    fun searchBills(
        note: String,
        category: String,
        minAmount: Double,
        maxAmount: Double,
        startDate: String,
        endDate: String
    ): Flow<List<BillEntity>>

    /**
     * Старые DAO
     * */
    @Query("SELECT * FROM bills_list")
    fun getAllLD(): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE month = :month")
    fun getMonthList(month : String): List<BillEntity>

    @Query("SELECT * FROM bills_list WHERE type = :type")
    fun getTypeList(type : Int): List<BillEntity>

    @Query("SELECT * FROM bills_list WHERE bookmark = :type")
    fun getBookmarks(type : Boolean): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills_list WHERE id = :id")
    fun getItem(id : Int): BillEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item : BillEntity)

    @Delete(entity = BillEntity::class)
    fun delete(item : BillEntity)

    @Update
    fun update(item : BillEntity)

    /**
     * Кандидат на удаление
     * */

    @Query("SELECT * FROM bills_list WHERE month = :month")
    fun getMonthLD(month : String): LiveData<List<BillEntity>>

}