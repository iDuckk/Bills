package com.billsAplication.presentation.billsList

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.R
import com.billsAplication.di.ApplicationScope
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.billsUseCases.GetTypeUseCase
import com.billsAplication.domain.model.BillsItem
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
//@ApplicationScope
class BillsListViewModel @Inject constructor(
    private val getAllDatabase : GetAllDataListUseCase,
    private val getMonth : GetMonthListUseCase,
    private val delete : DeleteBillItemUseCase,
    private val getTypeUseCase: GetTypeUseCase,
    private val application: Application
) : ViewModel() {

    private var date = LocalDate.now()

    lateinit var list: LiveData<List<BillsItem>>
    lateinit var listCategory: LiveData<List<BillsItem>>

    var currentDate: String

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

    private val TYPE_CATEGORY_EXPENSES = 2
    private val TYPE_CATEGORY_INCOME = 4

    init {
        getMonth(currentDate())
        currentDate = currentDate()
//        getCategoryType()
    }

    fun defaultMonth(){
        changeMonth = 0
        changeYear = 0
    }

    fun getAll() {
        list = getAllDatabase.invoke()
    }

    fun getCategoryExpenses() {
        listCategory = getTypeUseCase.invoke(TYPE_CATEGORY_EXPENSES)
    }
 //TODO личст для инком и экспенс и для общего
    fun getCategoryIncome() {
        listCategory = getTypeUseCase.invoke(TYPE_CATEGORY_INCOME)
    }

    fun getMonth(month: String) {
        list =  getMonth.invoke(mapMonthToSQL(month))
    }

    suspend fun delete(item: BillsItem){
        delete.invoke(item)
    }

    fun currentDate() : String{
        return mapMonthToTextView(date.month.toString())+ " " + date.year.toString()
    }

    fun changeMonthBar(typeChanges : Boolean) : String{
        if (typeChanges) changeMonth++ else changeMonth--
        //Set Year
        if(date.month.plus(changeMonth.toLong()).toString() == JANUARY
            && typeChanges
        ) changeYear++
        else if(date.month.plus(changeMonth.toLong()).toString() == DECEMBER
            && !typeChanges
        ) changeYear--
        //Set Month
        if (changeMonth > 0) {
            return mapMonthToTextView(date.month.plus(changeMonth.toLong()).toString()) +
                        " " + date.year.plus(Math.abs(changeYear).toLong()).toString()
        }
        else {
            return mapMonthToTextView(date.month.minus(Math.abs(changeMonth).toLong()).toString()) +
                        " " + date.year.minus(Math.abs(changeYear).toLong()).toString()
            }
    }

    fun mapMonthToSQL(month: String): String{
        when(month.dropLast(5)){
            application.getString(R.string.calendar_January) -> return JANUARY + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_february) -> return FEBRUARY + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_march) -> return MARCH + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_april) -> return APRIL + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_may) -> return MAY + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_june) -> return JUNE + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_july) -> return JULY + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_august) -> return AUGUST + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_september) -> return SEPTEMBER + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_october) -> return OCTOBER + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_november) -> return NOVEMBER + month.removePrefix(month.dropLast(5))
            application.getString(R.string.calendar_december) -> return DECEMBER + month.removePrefix(month.dropLast(5))
        }
        return ""
    }

    fun mapMonthToTextView(month: String): String{
        //Update resources for new language
        val config = Configuration()
        val locale = Locale(Locale.getDefault().language)
        config.locale = locale
        application.resources.updateConfiguration(Configuration(), application.resources.displayMetrics)
        when(month){
            JANUARY -> return application.getString(R.string.calendar_January)
            FEBRUARY -> return application.getString(R.string.calendar_february)
            MARCH -> return application.getString(R.string.calendar_march)
            APRIL -> return application.getString(R.string.calendar_april)
            MAY -> return application.getString(R.string.calendar_may)
            JUNE -> return application.getString(R.string.calendar_june)
            JULY -> return application.getString(R.string.calendar_july)
            AUGUST -> return application.getString(R.string.calendar_august)
            SEPTEMBER -> return application.getString(R.string.calendar_september)
            OCTOBER -> return application.getString(R.string.calendar_october)
            NOVEMBER -> return application.getString(R.string.calendar_november)
            DECEMBER -> return application.getString(R.string.calendar_december)
        }
        return ""
    }

}