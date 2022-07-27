package com.billsAplication.utils

import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class SortingAsc@Inject constructor() {
    operator fun invoke(list: MutableList<BillsItem>): List<BillsItem>{
        //Sorting of date and time
        for (i in 0..list.lastIndex - 1){
            var j = i + 1 //first element of While
            while (j > 0){
                if(date.invoke(list[j - 1].date + list[j - 1].time)
                    > date.invoke(list[j].date + list[j].time))
                    list[j] = list[j - 1].also { list[j - 1] = list[j] }
                j--
            }
        }
        return list
    }

    companion object{
        private val date = CreateDate()
    }
}