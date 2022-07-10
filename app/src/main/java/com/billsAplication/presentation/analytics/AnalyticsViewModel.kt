package com.billsAplication.presentation.analytics

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.R
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.billsUseCases.GetTypeUseCase
import com.billsAplication.domain.model.BillsItem
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class AnalyticsViewModel @Inject constructor(
    private val getMonth : GetMonthListUseCase,
    private val application: Application
) : ViewModel() {

    private var date = LocalDate.now()

    lateinit var list: LiveData<List<BillsItem>>

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

    init {
        getMonth(currentDate())
        currentDate = currentDate()
    }

    fun defaultMonth(){
        changeMonth = 0
        changeYear = 0
    }

    fun getMonth(month: String) {
        list =  getMonth.invoke(mapMonthToSQL(month))
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
            application.applicationContext.getString(R.string.calendar_January) -> return JANUARY + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_february) -> return FEBRUARY + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_march) -> return MARCH + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_april) -> return APRIL + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_may) -> return MAY + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_june) -> return JUNE + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_july) -> return JULY + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_august) -> return AUGUST + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_september) -> return SEPTEMBER + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_october) -> return OCTOBER + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_november) -> return NOVEMBER + month.removePrefix(month.dropLast(5))
            application.applicationContext.getString(R.string.calendar_december) -> return DECEMBER + month.removePrefix(month.dropLast(5))
        }
        return ""
    }

    fun mapMonthToTextView(month: String): String{
        when(month){
            JANUARY -> return application.applicationContext.getString(R.string.calendar_January)
            FEBRUARY -> return application.applicationContext.getString(R.string.calendar_february)
            MARCH -> return application.applicationContext.getString(R.string.calendar_march)
            APRIL -> return application.applicationContext.getString(R.string.calendar_april)
            MAY -> return application.applicationContext.getString(R.string.calendar_may)
            JUNE -> return application.applicationContext.getString(R.string.calendar_june)
            JULY -> return application.applicationContext.getString(R.string.calendar_july)
            AUGUST -> return application.applicationContext.getString(R.string.calendar_august)
            SEPTEMBER -> return application.applicationContext.getString(R.string.calendar_september)
            OCTOBER -> return application.applicationContext.getString(R.string.calendar_october)
            NOVEMBER -> return application.applicationContext.getString(R.string.calendar_november)
            DECEMBER -> return application.applicationContext.getString(R.string.calendar_december)
        }
        return ""
    }
}