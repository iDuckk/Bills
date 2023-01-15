package com.billsAplication.presentation.billsList

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.R
import com.billsAplication.domain.billsUseCases.*
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.utils.ColorState
import com.billsAplication.utils.Result
import com.billsAplication.utils.StateBillsList
import com.billsAplication.utils.TotalAmountBar
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

//@ApplicationScope
class BillsListViewModel @Inject constructor(
    private val getMonth: GetMonthListUseCase,
    private val delete: DeleteBillItemUseCase,
    private val getTypeListUseCase: GetTypeListUseCase,
    private val application: Application
) : ViewModel() {

    private var date = LocalDate.now()

    private val _stateList = MutableLiveData<StateBillsList>()
    val stateList: LiveData<StateBillsList>
        get() = _stateList
    private val _month = MutableLiveData<String>()
    val month: LiveData<String>
        get() = _month

    private val exception = CoroutineExceptionHandler { _, e ->
        _stateList.value =
            com.billsAplication.utils.Error("BillsListViewModel:getStateList: ${e.message!!}")
    }
    private var scope = CoroutineScope(Dispatchers.Main + exception)

    var listBills = ArrayList<BillsItem>()
    var listIncome = ArrayList<BillsItem>()
    var listExpense = ArrayList<BillsItem>()

    private var changeMonth = 0
    private var changeYear = 0

    private var JANUARY = "JANUARY"
    private var FEBRUARY = "FEBRUARY"
    private var MARCH = "MARCH"
    private var APRIL = "APRIL"
    private var MAY = "MAY"
    private var JUNE = "JUNE"
    private var JULY = "JULY"
    private var AUGUST = "AUGUST"
    private var SEPTEMBER = "SEPTEMBER"
    private var OCTOBER = "OCTOBER"
    private var NOVEMBER = "NOVEMBER"
    private var DECEMBER = "DECEMBER"
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val TYPE_EQUALS = 2
    private val NEXT_MONTH = 20
    private val PREV_MONTH = 21
    private val CURRENT_MONTH = 22
    private val DIRACTION_NEXT_MONTH = true
    private val DIRACTION_PREV_MONTH = false

    init {
        getStateList(currentDate())
        _month.value = currentDate()
    }

    fun changeMonth(direction: Int) {
        val month = when (direction) {
                NEXT_MONTH -> changeMonthBar(DIRACTION_NEXT_MONTH)
                PREV_MONTH -> changeMonthBar(DIRACTION_PREV_MONTH)
                else -> {
                    changeMonth = 0
                    changeYear = 0
                    currentDate()
                }
            }
        getStateList(month)
        _month.value = month
    }

    fun getStateList(month: String) {
        //month's list
        getMainList(month)
        //Get lists for spinner
        getCategoriesLists()
        //Total amounts values
        scope.launch {
            val exp = getExpenseList(month)
            val inc = getIncomeList(month)

            _stateList.value = TotalAmountBar(
                "%,.2f".format(Locale.ENGLISH, exp),
                "%,.2f".format(Locale.ENGLISH, inc),
                "%,.2f".format(Locale.ENGLISH, (inc - exp))
            )

            colorState(inc, exp)
        }

    }

    private fun getMainList(month: String) {
        scope.launch() {
            withContext(Dispatchers.IO) {
                val result = Result(getMonth.invoke(mapMonthToSQL(month)))
                withContext(Dispatchers.Main) {
                    listBills = result.list //This list for Spinner
                    _stateList.value = result
                }
            }
        }
    }

    private suspend fun getIncomeList(month: String): BigDecimal {
        return withContext(Dispatchers.IO) {
            var income = BigDecimal(0.0)
            getMonth.invoke(mapMonthToSQL(month)).forEachIndexed { index, billsItem ->
                if (TYPE_INCOME == billsItem.type)
                    income += BigDecimal(billsItem.amount.replace(",", ""))
            }
            income
        }
    }

    private suspend fun getExpenseList(month: String): BigDecimal {
        return withContext(Dispatchers.IO) {
            var expense = BigDecimal(0.0)
            getMonth.invoke(mapMonthToSQL(month)).forEachIndexed { index, billsItem ->
                if (TYPE_EXPENSES == billsItem.type)
                    expense += BigDecimal(billsItem.amount.replace(",", ""))
            }
            expense
        }
    }

    private fun colorState(inc: BigDecimal, exp: BigDecimal) {
        if (inc > exp) _stateList.value = ColorState(TYPE_INCOME)
        else if (inc < exp) _stateList.value = ColorState(TYPE_EXPENSES)
        else _stateList.value = ColorState(TYPE_EQUALS)

    }

    private fun getCategoriesLists() {
        scope.launch {
            listExpense = getTypeListUseCase.invoke(TYPE_EXPENSES) as ArrayList<BillsItem>
            listIncome = getTypeListUseCase.invoke(TYPE_INCOME) as ArrayList<BillsItem>
        }
    }

    fun incomeCategorySpinner(): ArrayList<String>{
        val list = ArrayList<String>()
        listIncome.forEach {
            list.add(it.category)
        }
        return list
    }

    fun expenseCategorySpinner(): ArrayList<String>{
        val list = ArrayList<String>()
        listExpense.forEach {
            list.add(it.category)
        }
        return list
    }

    suspend fun delete(item: BillsItem) {
        delete.invoke(item)
    }

    fun currentDate(): String {
        return mapMonthToTextView(date.month.toString()) + " " + date.year.toString()
    }

    private fun changeMonthBar(typeChanges: Boolean): String {
        if (typeChanges) changeMonth++ else changeMonth--
        //Set Year
        if (date.month.plus(changeMonth.toLong()).toString() == JANUARY
            && typeChanges
        ) changeYear++
        else if (date.month.plus(changeMonth.toLong()).toString() == DECEMBER
            && !typeChanges
        ) changeYear--
        //Set Month
        if (changeMonth > 0) {
            return mapMonthToTextView(date.month.plus(changeMonth.toLong()).toString()) +
                    " " + date.year.plus(Math.abs(changeYear).toLong()).toString()
        } else {
            return mapMonthToTextView(date.month.minus(Math.abs(changeMonth).toLong()).toString()) +
                    " " + date.year.minus(Math.abs(changeYear).toLong()).toString()
        }
    }

    fun mapMonthToSQL(month: String): String {
        return when (month.dropLast(5)) {
            application.getString(R.string.calendar_January) -> JANUARY + month.removePrefix(
                month.dropLast(5)
            )
            application.getString(R.string.calendar_february) -> FEBRUARY + month.removePrefix(
                month.dropLast(5)
            )
            application.getString(R.string.calendar_march) -> MARCH + month.removePrefix(
                month.dropLast(
                    5
                )
            )
            application.getString(R.string.calendar_april) -> APRIL + month.removePrefix(
                month.dropLast(
                    5
                )
            )
            application.getString(R.string.calendar_may) -> MAY + month.removePrefix(
                month.dropLast(
                    5
                )
            )
            application.getString(R.string.calendar_june) -> JUNE + month.removePrefix(
                month.dropLast(
                    5
                )
            )
            application.getString(R.string.calendar_july) -> JULY + month.removePrefix(
                month.dropLast(
                    5
                )
            )
            application.getString(R.string.calendar_august) -> AUGUST + month.removePrefix(
                month.dropLast(
                    5
                )
            )
            application.getString(R.string.calendar_september) -> SEPTEMBER + month.removePrefix(
                month.dropLast(5)
            )
            application.getString(R.string.calendar_october) -> OCTOBER + month.removePrefix(
                month.dropLast(5)
            )
            application.getString(R.string.calendar_november) -> NOVEMBER + month.removePrefix(
                month.dropLast(5)
            )
            application.getString(R.string.calendar_december) -> DECEMBER + month.removePrefix(
                month.dropLast(5)
            )
            else -> "Wrong month"
        }
    }

    fun mapMonthToTextView(month: String): String {
        //Update resources for new language
        val config = Configuration()
        val locale = Locale(Locale.getDefault().language)
        config.locale = locale
        application.resources.updateConfiguration(
            Configuration(),
            application.resources.displayMetrics
        )
        return when (month) {
            JANUARY -> application.getString(R.string.calendar_January)
            FEBRUARY -> application.getString(R.string.calendar_february)
            MARCH -> application.getString(R.string.calendar_march)
            APRIL -> application.getString(R.string.calendar_april)
            MAY -> application.getString(R.string.calendar_may)
            JUNE -> application.getString(R.string.calendar_june)
            JULY -> application.getString(R.string.calendar_july)
            AUGUST -> application.getString(R.string.calendar_august)
            SEPTEMBER -> application.getString(R.string.calendar_september)
            OCTOBER -> application.getString(R.string.calendar_october)
            NOVEMBER -> application.getString(R.string.calendar_november)
            DECEMBER -> application.getString(R.string.calendar_december)
            else -> ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}