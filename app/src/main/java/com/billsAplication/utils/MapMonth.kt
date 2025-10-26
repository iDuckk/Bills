package com.billsAplication.utils

import android.content.Context
import android.content.res.Configuration
import com.billsAplication.R
import java.util.Locale

class MapMonth {
    companion object {

        var JANUARY = "JANUARY"
        var FEBRUARY = "FEBRUARY"
        var MARCH = "MARCH"
        var APRIL = "APRIL"
        var MAY = "MAY"
        var JUNE = "JUNE"
        var JULY = "JULY"
        var AUGUST = "AUGUST"
        var SEPTEMBER = "SEPTEMBER"
        var OCTOBER = "OCTOBER"
        var NOVEMBER = "NOVEMBER"
        var DECEMBER = "DECEMBER"

        fun mapMonthToSQL(month: String, context: Context): String {
            when(month.dropLast(5)) {
                context.getString(R.string.calendar_January) -> return JANUARY + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_february) -> return FEBRUARY + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_march) -> return MARCH + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_april) -> return APRIL + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_may) -> return MAY + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_june) -> return JUNE + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_july) -> return JULY + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_august) -> return AUGUST + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_september) -> return SEPTEMBER + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_october) -> return OCTOBER + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_november) -> return NOVEMBER + month.removePrefix(month.dropLast(5))
                context.getString(R.string.calendar_december) -> return DECEMBER + month.removePrefix(month.dropLast(5))
            }
            return ""
        }

        fun mapMonthToTextView(month: String, context: Context): String{
            //Update resources for new language
            val config = Configuration()
            val locale = Locale(Locale.getDefault().language)
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            when(month){
                JANUARY -> return context.resources.getString(R.string.calendar_January)
                FEBRUARY -> return context.resources.getString(R.string.calendar_february)
                MARCH -> return context.resources.getString(R.string.calendar_march)
                APRIL -> return context.resources.getString(R.string.calendar_april)
                MAY -> return context.resources.getString(R.string.calendar_may)
                JUNE -> return context.resources.getString(R.string.calendar_june)
                JULY -> return context.resources.getString(R.string.calendar_july)
                AUGUST -> return context.resources.getString(R.string.calendar_august)
                SEPTEMBER -> return context.resources.getString(R.string.calendar_september)
                OCTOBER -> return context.resources.getString(R.string.calendar_october)
                NOVEMBER -> return context.resources.getString(R.string.calendar_november)
                DECEMBER -> return context.resources.getString(R.string.calendar_december)
            }
            return ""
        }
    }
}