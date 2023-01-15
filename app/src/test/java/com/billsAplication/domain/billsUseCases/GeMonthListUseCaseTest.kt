package com.billsAplication.domain.billsUseCases

import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GeMonthListUseCaseTest {

    @Mock
    lateinit var billsListRepository: BillsListRepository

    private val TYPE_INCOME = 1
    private val month = "DECEMBER 2022"
    private val list = ArrayList<BillsItem>()
    private val item = BillsItem(
        id = 1,
        type = TYPE_INCOME,
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
        MockitoAnnotations.openMocks(this)
        list.add(item)
        list.add(item.copy(id = 2))
        list.add(item.copy(id = 3))
    }

    @Test
    fun `should return the same list from repository`() = runTest{

        val useCase = GetMonthListUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getMonthList(month = month)).thenReturn(list)

        val actual = useCase.invoke(month = month)

        Assert.assertEquals(list, actual)
    }

    @Test
    fun `should return null list from repository if wrong month`() = runTest{

        val wrongMonth = "DECEMBER"

        val useCase = GetMonthListUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getMonthList(month = wrongMonth)).thenReturn(null)

        val actual = useCase.invoke(month = wrongMonth)

        Assert.assertEquals(null, actual)
    }

    @Test
    fun `if we received not empty list from repository`() = runTest{

        val useCase = GetMonthListUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getMonthList(month = month)).thenReturn(list)

        val actual = useCase.invoke(month = month).size

        Assert.assertEquals(list.size, actual)
    }

    @Test
    fun `if list is empty`() = runTest{

        val emptyList = ArrayList<BillsItem>()

        val useCase = GetMonthListUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getMonthList(month = month)).thenReturn(emptyList)

        val actual = useCase.invoke(month = month).size

        Assert.assertEquals(0, actual)
    }

}