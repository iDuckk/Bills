package com.billsAplication.presentation.addBill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.AddBillItemUseCase
import com.billsAplication.domain.billsUseCases.room.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.UpdateBillItemUseCase
import com.billsAplication.domain.model.BillsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddBillViewModel @Inject constructor(
    private val addBill: AddBillItemUseCase,
    private val updateBill: UpdateBillItemUseCase,
    private val getAllDatabase: GetAllDataListUseCase
) : ViewModel() {

    private var scope = CoroutineScope(Dispatchers.Main)

    private val _completeEditTextList = MutableLiveData<ArrayList<String>>()
    val completeEditTextList: LiveData<ArrayList<String>>
        get() = _completeEditTextList

    lateinit var list: List<BillsItem> //Just for Test
    init {
        initAutoCompleteEditText()
    }

    fun getAll() { //Just for Test
        list = getAllDatabase.invoke()
    }

    suspend fun add(billItem: BillsItem) {
        addBill.invoke(billItem)
    }

    suspend fun update(billItem: BillsItem) {
        updateBill.invoke(billItem)
    }

    private fun initAutoCompleteEditText() {
        scope.launch (Dispatchers.IO) {
            val currentList = ArrayList<String>()
            val wholeList = getAllDatabase.invoke()

            wholeList.forEach {
                if (it.note != EMPTY_STRING && it.type != TYPE_NOTE) {
                    currentList.add(it.note)
                }
            }
            _completeEditTextList.postValue(currentList)
        }
    }

    companion object {
        private val EMPTY_STRING = ""
        private val TYPE_NOTE = 3
        private val TAG = "AddBillViewModel"

    }

}