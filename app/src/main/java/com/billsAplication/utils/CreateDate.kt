package com.billsAplication.utils

import android.util.Log
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class CreateDate@Inject constructor() {
    operator fun invoke(date: String): Date {
        if(date != EMPTY_STRING) {  // 27/04/202211:59 AM
            val day = date.dropLast(16)
            val month = date.drop(3).dropLast(13)
            val year = date.drop(6).dropLast(8)
            val hour = date.drop(10).dropLast(6)
            val minute = date.drop(13).dropLast(3)

            val c = Calendar.getInstance()
            c.set(year.toInt(),month.toInt(),day.toInt(), hour.toInt(), minute.toInt())

            return c.time
        }
        return Date()
    }

    companion object{
        private const val EMPTY_STRING = ""
    }
}