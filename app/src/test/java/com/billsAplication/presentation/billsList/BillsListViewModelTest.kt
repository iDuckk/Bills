package com.billsAplication.presentation.billsList

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValue
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.billsUseCases.GetTypeListUseCase
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.utils.ColorState
import com.billsAplication.utils.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.math.BigDecimal
import java.time.LocalDate


@ExperimentalCoroutinesApi
@Config(sdk = intArrayOf(30))
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class BillsListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    val month = "DECEMBER 2002"
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val TYPE_EQUALS = 2
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
        image5 = ""
    )

    private val dispatcher = StandardTestDispatcher()

    private val list = ArrayList<BillsItem>()
    lateinit var getMonth: GetMonthListUseCase
    lateinit var delete: DeleteBillItemUseCase
    lateinit var getTypeListUseCase: GetTypeListUseCase
    lateinit var application: Application
    lateinit var viewModel: BillsListViewModel


    @Before
    fun setUp() {
        application = RuntimeEnvironment.application
        getMonth = Mockito.mock(GetMonthListUseCase::class.java)
        delete = Mockito.mock(DeleteBillItemUseCase::class.java)
        getTypeListUseCase = Mockito.mock(GetTypeListUseCase::class.java)

        viewModel = BillsListViewModel(
            getMonth = getMonth,
            delete = delete,
            getTypeListUseCase = getTypeListUseCase,
            application = application
        )

        list.add(item)
        list.add(item.copy(id = 2))
        list.add(item.copy(id = 3))

        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun mapMonthToTextView_Return_Correct_String() {

        val month = "DECEMBER"
        val expected = "December"

        val actual = viewModel.mapMonthToTextView(month = month)

        Assert.assertEquals(expected, actual)

    }

    @Test
    fun mapMonthToTextView_Return_Incorrect_String() {

        val month = "DECEMBE"
        val actualMonth = ""

        val actual = viewModel.mapMonthToTextView(month = month)

        Assert.assertEquals(actualMonth, actual)

    }

    @Test
    fun mapMonthToSQL_Return_Incorrect_String() {

        val month = "January 202"
        val actualMonth = "Wrong month"

        val expected = viewModel.mapMonthToSQL(month = month)

        Assert.assertEquals(actualMonth, expected)

    }

    @Test
    fun mapMonthToSQL_Return_Correct_String() {

        val month = "January 2023"
        val actualMonth = "JANUARY 2023"

        val actual = viewModel.mapMonthToSQL(month = month)

        Assert.assertEquals(actualMonth, actual)

    }

    @Test
    fun currentDate() {

        val date = LocalDate.now()
        val currentDate =
            viewModel.mapMonthToTextView(month = date.month.toString()) + " " + date.year.toString()

        val actual = viewModel.currentDate()

        Assert.assertEquals(currentDate, actual)

    }

    @Test
    fun getMainList() = runTest {

        Mockito.`when`(getMonth.invoke(month = viewModel.mapMonthToSQL(month = month)))
            .thenReturn(list)

        viewModel.getMainList(month = month)
        dispatcher.scheduler.advanceUntilIdle()

        val expected = list
        val actual = viewModel.stateList.getOrAwaitValue() as com.billsAplication.utils.Result

        Assert.assertEquals(expected, actual.list)
    }

    @Test
    fun getCategoriesListsIncome() = runTest {

        Mockito.`when`(getTypeListUseCase.invoke(type = TYPE_INCOME) as ArrayList<BillsItem>)
            .thenReturn(list)

        viewModel.getCategoriesListIncome()

        dispatcher.scheduler.advanceUntilIdle()

        Assert.assertEquals(list, viewModel.listIncome)

    }

    @Test
    fun getCategoriesListsExpenses() = runTest {

        Mockito.`when`(getTypeListUseCase.invoke(type = TYPE_EXPENSES))
            .thenReturn(list)

        viewModel.getCategoriesListExpenses()

        dispatcher.scheduler.advanceUntilIdle()

        Assert.assertEquals(list, viewModel.listExpense)

    }

    @Test
    fun getIncomeList() = runTest {

        Mockito.`when`(getMonth.invoke(month = viewModel.mapMonthToSQL(month = month)))
            .thenReturn(list)

        val expected = "300.0".toBigDecimal()
        val actual = viewModel.getIncomeList(month = month)

        dispatcher.scheduler.advanceUntilIdle()

        Assert.assertEquals(expected, actual)

    }

    @Test
    fun getExpenseList() = runTest {

        Mockito.`when`(getMonth.invoke(month = viewModel.mapMonthToSQL(month = month)))
            .thenReturn(list)

        val expected = "300.0".toBigDecimal()
        val actual = viewModel.getIncomeList(month = month)

        dispatcher.scheduler.advanceUntilIdle()

        Assert.assertEquals(expected, actual)

    }

    @Test
    fun colorStateIncome(){

        val inc = BigDecimal(300)
        val exp = BigDecimal(100)

        viewModel.colorState(inc, exp)

        val expected = TYPE_INCOME
        val actual = viewModel.stateList.getOrAwaitValue() as ColorState

        Assert.assertEquals(expected, actual.type)

    }

    @Test
    fun colorStateExpenses(){

        val inc = BigDecimal(300)
        val exp = BigDecimal(400)

        viewModel.colorState(inc, exp)

        val expected = TYPE_EXPENSES
        val actual = viewModel.stateList.getOrAwaitValue() as ColorState

        Assert.assertEquals(expected, actual.type)

    }

    @Test
    fun colorStateIncomeEquals(){

        val inc = BigDecimal(300)
        val exp = BigDecimal(300)

        viewModel.colorState(inc, exp)

        val expected = TYPE_EQUALS
        val actual = viewModel.stateList.getOrAwaitValue() as ColorState

        Assert.assertEquals(expected, actual.type)

    }

    @Test
    fun incomeCategorySpinner() {

        viewModel.listIncome = list

        val expected = ArrayList<String>()
        list.forEach{
            expected.add(it.category)
        }
        val actual = viewModel.incomeCategorySpinner()

        Assert.assertEquals(expected, actual)

    }

    @Test
    fun expensesCategorySpinner() = runTest{

        Mockito.`when`(getTypeListUseCase.invoke(type = TYPE_EXPENSES))
            .thenReturn(list)

//        viewModel.listExpense = list

        viewModel.getCategoriesListExpenses()
        dispatcher.scheduler.advanceUntilIdle()

        val expected = ArrayList<String>()
        list.forEach{
            expected.add(it.category)
        }
        val actual = viewModel.expenseCategorySpinner()

        Assert.assertEquals(expected, actual)

    }

    @Test
    fun changeMonthBar_plusMonth() {

        val date = LocalDate.now()

        val expected = viewModel.mapMonthToTextView(date.month.plus(1).toString())
        val actual = viewModel.changeMonthBar(true).dropLast(5)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun changeMonthBar_minusMonth() {

        val date = LocalDate.now()

        val expected = viewModel.mapMonthToTextView(date.month.minus(1).toString())
        val actual = viewModel.changeMonthBar(false).dropLast(5)

        Assert.assertEquals(expected, actual)
    }

}