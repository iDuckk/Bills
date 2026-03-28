package com.billsAplication.presentation.billsList

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsAplication.domain.billsUseCases.AddBillItemUseCase
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.billsUseCases.GetTypeListUseCase
import com.billsAplication.domain.billsUseCases.GetCategoryListUseCase
import com.billsAplication.domain.billsUseCases.GetBookmarksUseCase
import com.billsAplication.domain.billsUseCases.UpdateBillItemUseCase
import com.billsAplication.domain.billsUseCases.room.GetBillsListFlowUseCase
import com.billsAplication.domain.billsUseCases.room.SummaryAmountUseCase
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_EXPENSES
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_INCOME
import com.billsAplication.utils.MapMonth
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

//@ApplicationScope
class BillsListViewModel @Inject constructor(
    private val getMonthFlow: GetBillsListFlowUseCase,
    private val getMonth: GetMonthListUseCase,
    private val delete: DeleteBillItemUseCase,
    private val getTypeListUseCase: GetTypeListUseCase,
    private val application: Application,
    private val summaryAmount : SummaryAmountUseCase,
    private val addBill: AddBillItemUseCase,
    private val getCategoryListUseCase: GetCategoryListUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val updateBillItemUseCase: UpdateBillItemUseCase
) : ViewModel() {



    private val exception = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, "BillsListViewModel:: ${e.message!!}: ", e)
    }

    val bookmarks = getBookmarksUseCase.invoke(true)

    companion object {
        private const val TAG = "BillsListFragment"
    }

    fun getMonthListFlow(month: String) =
        getMonthFlow.invoke(month = MapMonth.mapMonthToSQL(month = month, context = application))

    /**
     * Income amount, expense amount, total amount, typeColorAddButton
     * */
    fun summaryAmount(month: String, onFinish: (String, String, String, Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exception) {
            val inc = summaryAmount.invoke(month = MapMonth.mapMonthToSQL(
                month = month, context = application), type = TYPE_INCOME)
            val exp = summaryAmount.invoke(month = MapMonth.mapMonthToSQL(
                month = month, context = application), type = TYPE_EXPENSES)
            withContext(Dispatchers.Main) {
                onFinish.invoke(
                    "%,.2f".format(Locale.ENGLISH, inc),
                    "%,.2f".format(Locale.ENGLISH, exp),
                    "%,.2f".format(Locale.ENGLISH, (inc - exp)),
                    if (inc > exp) TYPE_INCOME else TYPE_EXPENSES
                )
            }
        }
    }

    fun delete(item: BillsItem) {
        viewModelScope.launch(Dispatchers.IO + exception) {
            delete.invoke(item = item)
        }
    }

    fun add(billItem: BillsItem) {
        viewModelScope.launch(Dispatchers.IO + exception) {
            addBill.invoke(billItem)
        }
    }

    fun getCategoryList(type: Int, list: (List<BillsItem>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exception) { //.map { it.category }.distinct()
            list.invoke(getCategoryListUseCase.invoke(type)) //listOf("Food", "Transport", "Utilities", "Other"))
        }
    }

    fun updateBookmark(billItem: BillsItem) {
        viewModelScope.launch(Dispatchers.IO + exception) {
            updateBillItemUseCase.invoke(billItem)
        }
    }


}