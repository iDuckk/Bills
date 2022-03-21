package com.billsAplication.presentation.billsList

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.di.ApplicationScope
import com.billsAplication.domain.billsUseCases.GetAllDataListUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.BillsAdapter
import java.time.LocalDate
import javax.inject.Inject

@ApplicationScope
class BillsListViewModel @Inject constructor(
    private val getAllDatabase : GetAllDataListUseCase,
    private val getMonth : GetMonthListUseCase,
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private var date = LocalDate.now()

    lateinit var list : LiveData<List<BillsItem>>
    private var changeMonth = 0
    private var changeYear = 0

    fun getAll() {
        list =  getAllDatabase.invoke()
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


    //date.month.minus(1)
    //date.year.minus(1)


    //        val repo = BillsListRepositoryImpl(requireActivity().application)
//        val ITEMS : LiveData<List<BillsItem>> = repo.getAllDataList()
//        ITEMS.observe(requireActivity()) {
//            billAdapter.submitList(it.toMutableList())
//        }

}