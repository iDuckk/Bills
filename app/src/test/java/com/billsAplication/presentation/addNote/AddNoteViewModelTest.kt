package com.billsAplication.presentation.addNote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValue
import com.billsAplication.domain.billsUseCases.*
import com.billsAplication.domain.model.BillsItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.*

@ExperimentalCoroutinesApi
internal class AddNoteViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: AddNoteViewModel
    lateinit var addBill: AddBillItemUseCase
    lateinit var updateBill: UpdateBillItemUseCase
    lateinit var delete: DeleteBillItemUseCase
    lateinit var getBill: GetBillItemUsesCase

    val id = 1
    val repo = FakeBillsListRepository()
    private val COLOR_NOTE_BLUE = "blue"
    private val TYPE_NOTE = 3
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

        addBill = AddBillItemUseCase(repo = repo)
        updateBill = UpdateBillItemUseCase(repo = repo)
        delete = DeleteBillItemUseCase(repo = repo)
        getBill = GetBillItemUsesCase(repo = repo)

        viewModel = AddNoteViewModel(
            addBill = addBill,
            update = updateBill,
            delete = delete,
            getBill = getBill)
    }

    @Test
    fun getItem() = runTest{

        val expected = item
        val actual = viewModel.getItem(id = id)

        Assert.assertEquals(expected, actual)

    }

    @Test
    fun add() = runTest{

        val newNote = "Breezz"
        val itemNote = BillsItem(
            id = 0,
            type = TYPE_NOTE,
            month = "",
            date = "",
            time = "",
            category = "",
            amount = "",
            note = newNote,
            description = COLOR_NOTE_BLUE,
            bookmark = false,
            image1 = "",
            image2 = "",
            image3 = "",
            image4 = "",
            image5 = "")

        list.add(itemNote)

        viewModel.add(note = newNote, color = COLOR_NOTE_BLUE)

        val actual =  viewModel.getItem(id = 0)

        Assert.assertEquals(itemNote, actual)

    }

    @Test
    fun updateNotesList() = runTest {

        val newNote = "Breezz"
        val itemNote = BillsItem(
            id = id,
            type = TYPE_NOTE,
            month = "",
            date = "",
            time = "",
            category = "",
            amount = "",
            note = newNote,
            description = COLOR_NOTE_BLUE,
            bookmark = false,
            image1 = "",
            image2 = "",
            image3 = "",
            image4 = "",
            image5 = "")

        list.forEachIndexed { index, billsItem ->
            if(billsItem.id == itemNote.id) {
                list[index] = itemNote
            }
        }

        viewModel.updateNotesList(billItem = itemNote)

        val actual =  viewModel.getItem(id = 1)

        Assert.assertEquals(itemNote, actual)

    }

    @Test
    fun delete() = runTest{

        list.remove(item)

        viewModel.delete(billItem = item)

        val expected = list.contains(item)
        val actual = repo.list.contains(item)

        Assert.assertEquals(expected, actual)

    }

}