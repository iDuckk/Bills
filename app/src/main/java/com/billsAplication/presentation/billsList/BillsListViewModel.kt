package com.billsAplication.presentation.billsList

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.di.ApplicationScope
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.BillsAdapter
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@RequiresApi(Build.VERSION_CODES.O)
@ApplicationScope
class BillsListViewModel @Inject constructor(
    private val getAllDatabase : GetAllDataListUseCase,
    private val getMonth : GetMonthListUseCase,
    private val delete : DeleteBillItemUseCase,
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
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

    fun getAll() {
        list = getAllDatabase.invoke()
    }

    fun getMonth(month: String) {
        list =  getMonth.invoke(month)
    }

    suspend fun delete(item: BillsItem){
        delete.invoke(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun currentDate() : String{
        return date.month.toString()+ " " + date.year.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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