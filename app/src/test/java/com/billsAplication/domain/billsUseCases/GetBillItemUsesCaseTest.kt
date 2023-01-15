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
class GetBillItemUsesCaseTest {

    @Mock
    lateinit var billsListRepository: BillsListRepository

    private val TYPE_INCOME = 1
    private val id = 1
    private val item = BillsItem(
        id = id,
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
    }

    @Test
    fun `should return the same item from repository`() = runTest{

        val useCase = GetBillItemUsesCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getItem(id = id)).thenReturn(item)

        val actual = useCase.invoke(id = id)

        Assert.assertEquals(item, actual)
    }

    @Test
    fun `should return null from repository if id does not correct`() = runTest{

        val idDifferent = 2

        val useCase = GetBillItemUsesCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getItem(id = id)).thenReturn(null)

        val actual = useCase.invoke(id = idDifferent)

        Assert.assertEquals(null, actual)
    }

}