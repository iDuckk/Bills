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
class GetTypeListUseCaseTest {

    @Mock
    lateinit var billsListRepository: BillsListRepository

    private val TYPE_INCOME = 1
    private val TYPE_EXPENSE = 0
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

        val useCase = GetTypeListUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getTypeList(type = TYPE_INCOME)).thenReturn(list)

        val actual = useCase.invoke(type = TYPE_INCOME)

        Assert.assertEquals(list, actual)
    }

    @Test
    fun `should return empty list from repository if wrong type`() = runTest{

        val emptyList = ArrayList<BillsItem>()

        val useCase = GetTypeListUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getTypeList(type = TYPE_INCOME)).thenReturn(emptyList)

        val actual = useCase.invoke(type = TYPE_INCOME)

        Assert.assertEquals(0, actual.size)
    }

    @Test
    fun `should return null list from repository`() = runTest{

        val useCase = GetTypeListUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getTypeList(type = TYPE_EXPENSE)).thenReturn(null)

        val actual = useCase.invoke(type = TYPE_EXPENSE)

        Assert.assertEquals(null, actual)
    }

}