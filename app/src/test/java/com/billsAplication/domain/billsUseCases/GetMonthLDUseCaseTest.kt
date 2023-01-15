package com.billsAplication.domain.billsUseCases

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.repository.BillsListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class GetMonthLDUseCaseTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var billsListRepository: BillsListRepository

    private val liveData = MutableLiveData<List<BillsItem>>()
    private val list = ArrayList<BillsItem>()
    private val month = "DECEMBER 2022"
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
        MockitoAnnotations.openMocks(this)
        list.add(item)
        list.add(item.copy(id = 2))
        list.add(item.copy(id = 3))
    }

    @Test
    fun `should return the same list from repository`(){

        liveData.postValue(list)

        val useCase = GetMonthLDUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getMonthLD(month = month)).thenReturn(liveData)

        val actual = useCase.invoke(month = month)

        Assert.assertEquals(list, actual.value)
    }

    @Test
    fun `should return null from repository`(){

        val useCase = GetMonthLDUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getMonthLD(month = month)).thenReturn(null)

        val actual = useCase.invoke(month = month)

        Assert.assertEquals(null, actual)
    }

    @Test
    fun `if return empty list from repository`(){

        val useCase = GetMonthLDUseCase(repo = billsListRepository)
        Mockito.`when`(billsListRepository.getMonthLD(month = month)).thenReturn(liveData)

        val actual = useCase.invoke(month = month)

        Assert.assertEquals(null, actual.value?.size)
    }

}