package com.billsAplication.presentation.addBill

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.billsAplication.domain.billsUseCases.*
import com.billsAplication.domain.billsUseCases.room.GetAllDataListUseCase
import com.billsAplication.domain.model.BillsItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*

@ExperimentalCoroutinesApi
internal class AddBillViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: AddBillViewModel
    lateinit var addBill: AddBillItemUseCase
    lateinit var updateBill: UpdateBillItemUseCase
    lateinit var getAllDatabase: GetAllDataListUseCase

    var liveData = MutableLiveData<List<BillsItem>>()
    private val list = ArrayList<BillsItem>()
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
    fun setUp() {
        list.add(item)
        list.add(item.copy(id = 2))
        list.add(item.copy(id = 3))
        liveData.postValue(list)

        val repo = FakeBillsListRepository()

        addBill = AddBillItemUseCase(repo = repo)
        updateBill = UpdateBillItemUseCase(repo = repo)
        getAllDatabase = GetAllDataListUseCase(repo = repo)

        viewModel = AddBillViewModel(
            addBill = addBill,
            updateBill = updateBill,
            getAllDatabase = getAllDatabase)
    }

    @Test
    fun add() = runTest{

        val newItem = item.copy(id = 4)
        list.add(newItem)

        viewModel.add(newItem)

        viewModel.getAll()

        val expected = list
        val actual = viewModel.list as ArrayList<BillsItem>

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun update() = runTest{
        val newItem = item.copy(id = 1, category = "Taxi")
        list.forEachIndexed { index, billsItem ->
            if(billsItem.id == newItem.id) {
                list[index] = newItem
            }
        }

        viewModel.update(newItem)

        viewModel.getAll()

        val expected = list
        val actual = viewModel.list as ArrayList<BillsItem>

        Assert.assertEquals(expected, actual)

    }
}