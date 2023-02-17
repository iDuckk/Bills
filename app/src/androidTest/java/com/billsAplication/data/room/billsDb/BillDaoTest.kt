package com.billsAplication.data.room.billsDb

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValue
import com.billsAplication.data.room.model.BillEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*

@ExperimentalCoroutinesApi
class BillDaoTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database: BillDatabase
    private lateinit var dao: BillDao

    private val item = BillEntity(
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
    fun insertBillEntity() = runTest{

        dao.insert(item = item)

        val receivedBillsItem = dao.getAllLD().getOrAwaitValue()

        Assert.assertEquals(item, receivedBillsItem[0])

    }

    @Test
    fun deleteBillEntity() = runTest{

        dao.insert(item = item)

        var receivedBillsItem = dao.getAllLD().getOrAwaitValue()

        Assert.assertEquals(1, receivedBillsItem.size)

        dao.delete(item = item)

        receivedBillsItem = dao.getAllLD().getOrAwaitValue()

        Assert.assertEquals(0, receivedBillsItem.size)

    }

    @Test
    fun updateBillEntity() = runTest{

        dao.insert(item = item)

        var receivedBillsItem = dao.getAllLD().getOrAwaitValue()

        Assert.assertEquals(item, receivedBillsItem[0])

        val newItem = item.copy(
            id = 1,
            type = 0,
            month = "JANUARY 2023",
            date = "15/01/2023"
        )

        dao.update(item = newItem)

        receivedBillsItem = dao.getAllLD().getOrAwaitValue()

        Assert.assertEquals(newItem, receivedBillsItem[0])

    }


    @Test
    fun getItem() = runTest{

        val id = 1

        dao.insert(item = item)

        val receivedBillsItem = dao.getItem(id = id)

        Assert.assertEquals(item, receivedBillsItem)

    }

    @Test
    fun getBookmarksFalse() = runTest{

        val typeBookmarks = false

        dao.insert(item)

        val receivedBillsItem = dao.getBookmarks(type = typeBookmarks).getOrAwaitValue()

        Assert.assertEquals(item, receivedBillsItem[0])

    }

    @Test
    fun getBookmarksTrue() = runTest{

        val typeBookmarks = true
        val newItem = item.copy(bookmark = typeBookmarks)

        dao.insert(item = newItem)

        val receivedBillsItem = dao.getBookmarks(type = typeBookmarks).getOrAwaitValue()

        Assert.assertEquals(newItem, receivedBillsItem[0])

    }

    @Test
    fun getTypeList() = runTest{

        val TYPE_INCOME = 1
        dao.insert(item = item)

        val receivedBillsItem = dao.getTypeList(type = TYPE_INCOME)

        Assert.assertEquals(item, receivedBillsItem[0])

    }

    @Test
    fun getType() = runTest{

        val TYPE_INCOME = 1
        dao.insert(item = item)

        val receivedBillsItem = dao.getType(type = TYPE_INCOME).getOrAwaitValue()

        Assert.assertEquals(item, receivedBillsItem[0])

    }

    @Test
    fun getMonthList() = runTest{

        val month = "DECEMBER 2022"
        dao.insert(item = item)

        val receivedBillsItem = dao.getMonthList(month = month)

        Assert.assertEquals(item, receivedBillsItem[0])

    }

    @Test
    fun getMonthLD() = runTest{

        val month = "DECEMBER 2022"
        dao.insert(item = item)

        val receivedBillsItem = dao.getMonthLD(month = month).getOrAwaitValue()

        Assert.assertEquals(item, receivedBillsItem[0])

    }

    @Test
    fun getAll() = runTest{

        dao.insert(item = item)

        val receivedBillsItem = dao.getAllLD().getOrAwaitValue()

        Assert.assertEquals(item, receivedBillsItem[0])

    }

    @Test
    fun checkpoint() = runTest{

        val queryCheckPoint = "pragma wal_checkpoint(full)"

        val receivedBillsItem = dao.checkpoint(SimpleSQLiteQuery(queryCheckPoint))

        Assert.assertEquals(0, receivedBillsItem)

    }


}