package com.billsAplication.presentation.billsList

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.billsUseCases.GetTypeListUseCase
import com.billsAplication.domain.repository.BillsListRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class BillsListViewModelTest {

    private lateinit var viewModel: BillsListViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var billsListRepository: BillsListRepository
//    @Mock
//    lateinit var getMonth: GetMonthListUseCase
//    @Mock
//    lateinit var delete: DeleteBillItemUseCase
//    @Mock
//    lateinit var getTypeListUseCase: GetTypeListUseCase

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)

//        val getMonth = mock<GetMonthListUseCase>()
//        val delete = mock<DeleteBillItemUseCase>()
//        val getTypeListUseCase = mock<GetTypeListUseCase>()
        viewModel = BillsListViewModel(
            GetMonthListUseCase(billsListRepository),
            DeleteBillItemUseCase(billsListRepository),
            GetTypeListUseCase(billsListRepository),
            ApplicationProvider.getApplicationContext())
    }

//    @Test
//    fun mapMonthToTextView_Return_Correct_String() {
//        Assert.assertEquals(4, 2 + 2)
//        val month = "DECEMBER 2022"
//        val actualMonth = "December 2022"
//
//        val expected = viewModel.mapMonthToTextView(month = month)
//
//        Assert.assertEquals(actualMonth, expected)

//    }
}