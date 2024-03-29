package com.billsAplication.presentation.analytics

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.R
import com.billsAplication.domain.billsUseCases.GetMonthLDUseCase
import com.billsAplication.domain.billsUseCases.room.GetMonthListByTypeCategoryUseCase
import com.billsAplication.domain.billsUseCases.room.GetMonthListByTypeUseCase
import com.billsAplication.domain.billsUseCases.room.SummaryAmountUseCase
import com.billsAplication.domain.model.BillsItem
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

class AnalyticsViewModel @Inject constructor(
    private val getMonthListByTypeUseCase : GetMonthListByTypeUseCase,
    private val summaryAmount : SummaryAmountUseCase,
    private val getMonthListByTypeCategoryUseCase : GetMonthListByTypeCategoryUseCase,
    private val application: Application
) : ViewModel() {

    private var date = LocalDate.now()

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
        currentDate = currentDate()
    }

    fun summaryAmount(month: String, type: Int) =
                summaryAmount.invoke(month = mapMonthToSQL(month), type = type)

    fun getMonthListByType(month: String, type: Int) =
        getMonthListByTypeUseCase.invoke(month = mapMonthToSQL(month), type = type)

    fun getMonthListByTypeCategory(month: String, type: Int, category: String) =
        getMonthListByTypeCategoryUseCase.invoke(month = mapMonthToSQL(month), type = type, category = category)

    fun defaultMonth(){
        changeMonth = 0
        changeYear = 0
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

    private fun mapMonthToSQL(month: String): String{
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

    private fun mapMonthToTextView(month: String): String{
        //Update resources for new language
        val config = Configuration()
        val locale = Locale(Locale.getDefault().language)
        config.locale = locale
        application.resources.updateConfiguration(config, application.resources.displayMetrics)
        when(month){
            JANUARY -> return application.resources.getString(R.string.calendar_January)
            FEBRUARY -> return application.resources.getString(R.string.calendar_february)
            MARCH -> return application.resources.getString(R.string.calendar_march)
            APRIL -> return application.resources.getString(R.string.calendar_april)
            MAY -> return application.resources.getString(R.string.calendar_may)
            JUNE -> return application.resources.getString(R.string.calendar_june)
            JULY -> return application.resources.getString(R.string.calendar_july)
            AUGUST -> return application.resources.getString(R.string.calendar_august)
            SEPTEMBER -> return application.resources.getString(R.string.calendar_september)
            OCTOBER -> return application.resources.getString(R.string.calendar_october)
            NOVEMBER -> return application.resources.getString(R.string.calendar_november)
            DECEMBER -> return application.resources.getString(R.string.calendar_december)
        }
        return ""
    }
}