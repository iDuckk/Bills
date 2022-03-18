package com.billsAplication.presentation.billsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class BillsListViewModel @Inject constructor(
    private val getAllDatabase : GetAllDataListUseCase,
    private val getMonth : GetMonthListUseCase,
) : ViewModel() {

    fun getAll(): LiveData<List<BillsItem>> {
        return getAllDatabase.invoke()
    }

    //        val repo = BillsListRepositoryImpl(requireActivity().application)
//        val ITEMS : LiveData<List<BillsItem>> = repo.getAllDataList()
//        ITEMS.observe(requireActivity()) {
//            billAdapter.submitList(it.toMutableList())
//        }

}