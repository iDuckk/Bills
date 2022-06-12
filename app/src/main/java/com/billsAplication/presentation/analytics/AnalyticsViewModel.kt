package com.billsAplication.presentation.analytics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.billsUseCases.GetTypeUseCase
import com.billsAplication.domain.model.BillsItem
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class AnalyticsViewModel @Inject constructor(
    private val getMonth : GetMonthListUseCase,
) : ViewModel() {

    private var date = LocalDate.now()

    lateinit var list: LiveData<List<BillsItem>>

    var currentDate: String

    private var changeMonth = 0
    private var changeYear = 0

    init {
        getMonth(currentDate())
        currentDate = currentDate()
    }

    fun defaultMonth(){
        changeMonth = 0
        changeYear = 0
    }

    fun getMonth(month: String) {
        list =  getMonth.invoke(month)
    }

    fun currentDate() : String{
        return date.month.toString()+ " " + date.year.toString()
    }

    fun changeMonthBar(typeChanges : Boolean) : String{
        if (typeChanges) changeMonth++ else changeMonth--
        //Set Year
        if(date.month.plus(changeMonth.toLong()).toString() == "JANUARY"
            && typeChanges
        ) changeYear++
        else if(date.month.plus(changeMonth.toLong()).toString() == "DECEMBER"
            && !typeChanges
        ) changeYear--
        //Set Month
        if (changeMonth > 0) {
            return date.month.plus(changeMonth.toLong()).toString() +
                    " " + date.year.plus(Math.abs(changeYear).toLong()).toString()
        }
        else {
            return date.month.minus(Math.abs(changeMonth).toLong()).toString() +
                    " " + date.year.minus(Math.abs(changeYear).toLong()).toString()
        }
    }
}