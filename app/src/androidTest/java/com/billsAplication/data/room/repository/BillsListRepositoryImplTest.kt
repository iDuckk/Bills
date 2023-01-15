package com.billsAplication.data.room.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValue
import com.billsAplication.data.room.billsDb.BillDao
import com.billsAplication.data.room.billsDb.BillDatabase
import com.billsAplication.data.room.mapper.BillMapper
import com.billsAplication.domain.model.BillsItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import kotlin.math.exp

@ExperimentalCoroutinesApi
class BillsListRepositoryImplTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database: BillDatabase
    private lateinit var dao: BillDao
    private val billsListRepositoryImpl by lazy {
        BillsListRepositoryImpl(billDao = dao, mapper = BillMapper())
    }

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

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BillDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.billDao()
    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun addItem() = runTest {

        billsListRepositoryImpl.addItem(item = item)

        val expected = billsListRepositoryImpl.getAllDataList().getOrAwaitValue()

        Assert.assertEquals(item, expected[0])

    }

    @Test
    fun getItem() = runTest {

        val id = 1

        billsListRepositoryImpl.addItem(item = item)

        val expected = billsListRepositoryImpl.getItem(id = id)

        Assert.assertEquals(item, expected)

    }

    @Test
    fun deleteItem() = runTest {

        billsListRepositoryImpl.addItem(item = item)

        billsListRepositoryImpl.deleteItem(item = item)

        val expected = billsListRepositoryImpl.getAllDataList().getOrAwaitValue()

        Assert.assertEquals(0, expected.size)

    }

    @Test
    fun getMonthLD() = runTest {

        val month = "DECEMBER 2022"

        billsListRepositoryImpl.addItem(item = item)

        val expected = billsListRepositoryImpl.getMonthLD(month = month).getOrAwaitValue()

        Assert.assertEquals(item, expected[0])

    }

    @Test
    fun getMonthList() = runTest {

        val month = "DECEMBER 2022"

        billsListRepositoryImpl.addItem(item = item)

        val expected = billsListRepositoryImpl.getMonthList(month = month)

        Assert.assertEquals(item, expected[0])

    }

    @Test
    fun getTypeList() = runTest {

        val type = 1

        billsListRepositoryImpl.addItem(item = item)

        val expected = billsListRepositoryImpl.getTypeList(type = type)

        Assert.assertEquals(item, expected[0])

    }

    @Test
    fun getType() = runTest {

        val type = 1

        billsListRepositoryImpl.addItem(item = item)

        val expected = billsListRepositoryImpl.getType(type = type).getOrAwaitValue()

        Assert.assertEquals(item, expected[0])

    }

    @Test
    fun getBookmarks() = runTest {

        val type = false

        billsListRepositoryImpl.addItem(item = item)

        val expected = billsListRepositoryImpl.getBookmarks(type = type).getOrAwaitValue()

        Assert.assertEquals(item, expected[0])

    }

    @Test
    fun updateItem() = runTest {

        val newItem = item.copy(type = 0, month = "JANUARY 2023")

        billsListRepositoryImpl.addItem(item = item)

        billsListRepositoryImpl.updateItem(item = newItem)

        val expected = billsListRepositoryImpl.getAllDataList().getOrAwaitValue()

        Assert.assertEquals(newItem, expected[0])

    }

}