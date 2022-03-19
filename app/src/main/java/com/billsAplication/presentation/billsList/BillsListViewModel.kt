package com.billsAplication.presentation.billsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.di.ApplicationScope
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.BillsAdapter
import javax.inject.Inject

@ApplicationScope
class BillsListViewModel @Inject constructor(
    private val getAllDatabase : GetAllDataListUseCase,
    private val getMonth : GetMonthListUseCase,
) : ViewModel() {

    lateinit var list : LiveData<List<BillsItem>>
    val billAdapter : BillsAdapter = BillsAdapter()

    fun getAll() {
        list =  getAllDatabase.invoke()
    }

    //        val repo = BillsListRepositoryImpl(requireActivity().application)
//        val ITEMS : LiveData<List<BillsItem>> = repo.getAllDataList()
//        ITEMS.observe(requireActivity()) {
//            billAdapter.submitList(it.toMutableList())
//        }

}